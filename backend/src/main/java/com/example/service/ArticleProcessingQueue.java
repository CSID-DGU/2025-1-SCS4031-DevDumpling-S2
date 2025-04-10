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
        try {
            GeminiClient.ArticleAnalysis analysis = geminiClient.analyzeArticle(article.getContent());
            if (analysis != null) {
                article.setExplanation(analysis.getExplanation());
                article.setSummary(analysis.getSummary());
                article.setTermExplanations(analysis.getTermExplanationsJson());
                articleRepository.save(article);
                log.info("[큐] 기사 처리 완료: {}", article.getTitle());
                
                // 기사 분석이 완료된 후 퀴즈 생성
                Thread.sleep(10000);  // API 호출 간격 유지
                quizService.generateQuizzesForArticle(article);
                log.info("[큐] 퀴즈 생성 완료: {}", article.getTitle());
            }
        } catch (Exception e) {
            log.error("[큐] 기사 처리 중 에러 발생: {}", article.getTitle(), e);
            if (e instanceof HttpClientErrorException && ((HttpClientErrorException) e).getStatusCode().value() == 429) {
                log.warn("[큐] Rate limit 발생. 30초 후 재시도 예정: {}", article.getTitle());
                try {
                    Thread.sleep(30000); // 30초 대기
                    addArticle(article); // 큐에 다시 추가
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
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