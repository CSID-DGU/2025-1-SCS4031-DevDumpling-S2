package com.example.service;

import com.example.api.GeminiClient;
import com.example.entity.Article;
import com.example.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RssArticleService {

    private final ArticleRepository articleRepository;
    private final GeminiClient geminiClient;
    private final QuizService quizService;
    private final ArticleProcessingQueue processingQueue;

    public boolean existsByTitle(String title) {
        return articleRepository.existsByTitle(title);
    }

    @Transactional
    public void save(Article article) {
        articleRepository.save(article);
        processingQueue.addArticle(article);
    }

    public Article processArticle(Article article) {
        try {
            log.info("[기사 서비스] 저장 시작 - {}", article.getTitle());

            // 기사 URL에서 본문 크롤링
            String content = fetchContentFromUrl(article.getSourceUrl());
            if (content != null && !content.isBlank()) {
                article.setContent(content);
                log.info("[기사 서비스] 본문 크롤링 성공: {}", article.getTitle());
            } else {
                log.warn("[기사 서비스] 본문 크롤링 실패: {}", article.getTitle());
                article.setContent("본문 크롤링 실패");
            }

            // Gemini API 호출은 본문이 있을 때만 진행
            if (!article.getContent().equals("본문 크롤링 실패")) {
                try {
                    String summary = geminiClient.summarize(article.getContent());
                    article.setSummary(summary);
                } catch (Exception e) {
                    if (e.getMessage().contains("429")) {
                        log.warn("[기사 서비스] Gemini API 할당량 초과. 60초 대기 후 재시도");
                        Thread.sleep(60000);
                        try {
                            String summary = geminiClient.summarize(article.getContent());
                            article.setSummary(summary);
                        } catch (Exception retryE) {
                            log.warn("[기사 서비스] 요약 생성 재시도 실패: {} - {}", article.getTitle(), retryE.getMessage());
                            article.setSummary("요약 실패 - API 할당량 초과");
                        }
                    } else {
                        log.warn("[기사 서비스] 요약 생성 실패: {} - {}", article.getTitle(), e.getMessage());
                        article.setSummary("요약 실패");
                    }
                }

                try {
                    String explanation = geminiClient.explainSimply(article.getContent());
                    article.setExplanation(explanation);
                } catch (Exception e) {
                    if (e.getMessage().contains("429")) {
                        log.warn("[기사 서비스] Gemini API 할당량 초과. 60초 대기 후 재시도");
                        Thread.sleep(60000);
                        try {
                            String explanation = geminiClient.explainSimply(article.getContent());
                            article.setExplanation(explanation);
                        } catch (Exception retryE) {
                            log.warn("[기사 서비스] 설명 생성 재시도 실패: {} - {}", article.getTitle(), retryE.getMessage());
                            article.setExplanation("설명 실패 - API 할당량 초과");
                        }
                    } else {
                        log.warn("[기사 서비스] 설명 생성 실패: {} - {}", article.getTitle(), e.getMessage());
                        article.setExplanation("설명 실패");
                    }
                }
            } else {
                article.setSummary("본문 크롤링 실패");
                article.setExplanation("본문 크롤링 실패");
            }

            Article saved = articleRepository.save(article);
            try {
                if (!article.getContent().equals("본문 크롤링 실패")) {
                    quizService.generateQuizzesForArticle(saved);
                }
            } catch (Exception e) {
                log.warn("[기사 서비스] 퀴즈 생성 실패: {} - {}", article.getTitle(), e.getMessage());
            }
            return saved;

        } catch (Exception e) {
            log.error("[기사 서비스] 기사 저장 실패: {} - {}", article.getTitle(), e.getMessage());
            return null;
        }
    }

    public String fetchContentFromUrl(String url) {
        try {
            log.info("[기사 서비스] 본문 크롤링 시작: {}", url);
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                    .timeout(30000)
                    .get();

            Elements contentElements;
            if (url.contains("etnews.com")) {
                // 이티뉴스 기사 본문
                contentElements = doc.select("#articleBody");
                if (contentElements.isEmpty()) {
                    contentElements = doc.select("#content");
                }
            } else if (url.contains("hani.co.kr")) {
                // 한겨레 기사 본문
                contentElements = doc.select("div.text");
                if (contentElements.isEmpty()) {
                    contentElements = doc.select("div.article-text");
                }
            } else if (url.contains("mk.co.kr")) {
                // 매일경제 기사 본문
                contentElements = doc.select("div#article_body");
            } else {
                contentElements = new Elements();
            }

            if (contentElements.isEmpty()) {
                log.warn("[기사 서비스] 본문 요소를 찾을 수 없음: {}", url);
                return null;
            }

            String content = contentElements.text();
            content = cleanContent(content);
            
            if (content.isBlank()) {
                log.warn("[기사 서비스] 본문이 비어있음: {}", url);
                return null;
            }

            log.info("[기사 서비스] 본문 크롤링 성공 (길이: {}): {}", content.length(), url);
            return content;
        } catch (Exception e) {
            log.error("[기사 서비스] 본문 크롤링 실패: {} - {}", url, e.getMessage());
            return null;
        }
    }

    private String cleanContent(String content) {
        if (content == null) return null;
        return content
                .replaceAll("기사를 읽어드립니다.*?audio element\\.", "") // 오디오 플레이어 텍스트 제거
                .replaceAll("\\[.*?\\]", "") // [...]형태의 문구 제거
                .replaceAll("▶.*", "") // ▶로 시작하는 문구 제거
                .replaceAll("©.*", "") // ©로 시작하는 문구 제거
                .replaceAll("\\s+", " ") // 연속된 공백을 하나로
                .replaceAll("^\\s+|\\s+$", "") // 앞뒤 공백 제거
                .trim();
    }

    public String testCrawling(String url) {
        return fetchContentFromUrl(url);
    }

    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    public Article findById(Long id) {
        return articleRepository.findById(id).orElseThrow();
    }
}
