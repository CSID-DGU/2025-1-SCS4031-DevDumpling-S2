package com.example.service;

import com.example.entity.Article;
import com.example.repository.ArticleRepository;
import com.example.api.GeminiClient;
import com.example.api.GeminiClient.ArticleAnalysis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleProcessingQueue {
    private final BlockingQueue<Article> queue = new LinkedBlockingQueue<>();
    private final ArticleRepository articleRepository;
    private final GeminiClient geminiClient;
    private final QuizService quizService;
    private Thread processingThread;
    private volatile boolean isRunning = true;

    @PostConstruct
    public void init() {
        startProcessing();
        log.info("[큐] 처리 스레드 초기화 완료");
    }

    public void addArticle(Article article) {
        try {
            queue.put(article);
            log.info("[큐] 기사 추가됨: {}", article.getTitle());
        } catch (InterruptedException e) {
            log.error("[큐] 기사 추가 실패: {}", article.getTitle(), e);
            Thread.currentThread().interrupt();
        }
    }

    public void startProcessing() {
        if (processingThread == null || !processingThread.isAlive()) {
            isRunning = true;
            processingThread = new Thread(() -> {
                while (isRunning) {
                    try {
                        Article article = queue.take();
                        log.info("[큐] 기사 처리 시작: {}", article.getTitle());
                        
                        // API 호출 간격 증가 (10초)
                        Thread.sleep(10000);
                        
                        processArticle(article);
                    } catch (InterruptedException e) {
                        log.error("[큐] 처리 중단됨", e);
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        log.error("[큐] 기사 처리 실패", e);
                    }
                }
            });
            processingThread.setDaemon(true);
            processingThread.start();
            log.info("[큐] 처리 시작됨");
        }
    }

    private void processArticle(Article article) {
        int maxRetries = 3;
        int currentRetry = 0;
        
        while (currentRetry < maxRetries) {
            try {
                log.info("[큐] 기사 처리 시작 (재시도 {}/{}): {} (현재 상태: {})", 
                    currentRetry + 1, maxRetries, article.getTitle(), article.getStatus());
                
                GeminiClient.ArticleAnalysis analysis = geminiClient.analyzeArticle(article.getContent());
                if (analysis != null) {
                    // 필드 검증
                    if (isInvalidField(analysis.getExplanation()) || 
                        isInvalidField(analysis.getSummary()) || 
                        isInvalidField(analysis.getTermExplanationsJson())) {
                        log.warn("[큐] 일부 필드가 유효하지 않음 - 설명: {}, 요약: {}, 용어: {}", 
                            analysis.getExplanation(), analysis.getSummary(), analysis.getTermExplanationsJson());
                        throw new IllegalStateException("API 응답의 일부 필드가 유효하지 않습니다.");
                    }
                    
                    article.setExplanation(analysis.getExplanation());
                    article.setSummary(analysis.getSummary());
                    article.setTermExplanations(analysis.getTermExplanationsJson());
                    article.setStatus(Article.ProcessingStatus.COMPLETED);
                    
                    // 상태 변경 확인을 위한 로깅
                    log.info("[큐] 기사 상태 변경: {} -> {}", article.getTitle(), article.getStatus());
                    
                    articleRepository.save(article);
                    log.info("[큐] 기사 저장 완료 (상태: {}): {}", article.getStatus(), article.getTitle());
                    
                    // 저장 후 필드 검증
                    Article savedArticle = articleRepository.findById(article.getId()).orElse(null);
                    if (savedArticle != null) {
                        if (isInvalidField(savedArticle.getExplanation()) || 
                            isInvalidField(savedArticle.getSummary()) || 
                            isInvalidField(savedArticle.getTermExplanations())) {
                            log.warn("[큐] 저장된 기사의 일부 필드가 유효하지 않음 - ID: {}, 제목: {}", 
                                savedArticle.getId(), savedArticle.getTitle());
                            throw new IllegalStateException("저장된 기사의 일부 필드가 유효하지 않습니다.");
                        }
                        log.info("[큐] 저장된 기사 상태 확인: {} (상태: {})", 
                            savedArticle.getTitle(), savedArticle.getStatus());
                    }
                    
                    // 기사 분석이 완료된 후 퀴즈 생성
                    Thread.sleep(10000);  // API 호출 간격 유지
                    quizService.generateQuizzesForArticle(article);
                    log.info("[큐] 퀴즈 생성 완료: {}", article.getTitle());
                    return;
                } else {
                    log.warn("[큐] Gemini API 분석 결과가 null입니다: {}", article.getTitle());
                }
            } catch (Exception e) {
                log.error("[큐] 기사 처리 중 에러 발생 (재시도 {}/{}): {} - {}", 
                    currentRetry + 1, maxRetries, article.getTitle(), e.getMessage());
                
                if (e instanceof HttpClientErrorException && 
                    ((HttpClientErrorException) e).getStatusCode().value() == 429) {
                    log.warn("[큐] Rate limit 발생. 30초 후 재시도 예정: {}", article.getTitle());
                    try {
                        Thread.sleep(30000); // 30초 대기
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            currentRetry++;
        }
        
        // 모든 재시도 실패 시
        log.error("[큐] 기사 처리 실패 (최대 재시도 횟수 초과): {}", article.getTitle());
        article.setStatus(Article.ProcessingStatus.FAILED);
        article.setExplanation("기사 처리에 실패했습니다.");
        article.setSummary("기사 처리에 실패했습니다.");
        articleRepository.save(article);
        
        // 저장 후 상태 확인
        Article savedArticle = articleRepository.findById(article.getId()).orElse(null);
        if (savedArticle != null) {
            log.info("[큐] 실패한 기사 상태 확인: {} (상태: {})", 
                savedArticle.getTitle(), savedArticle.getStatus());
        }
    }

    private boolean isInvalidField(String field) {
        return field == null || 
               field.isEmpty() || 
               field.equals("처리 중...") || 
               field.equals("[]") || 
               field.equals("기사 처리에 실패했습니다.");
    }

    public void stopProcessing() {
        isRunning = false;
        if (processingThread != null) {
            processingThread.interrupt();
            try {
                processingThread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
} 