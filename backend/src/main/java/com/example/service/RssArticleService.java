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
        processingQueue.addArticle(article);  // 큐에서 처리 후 퀴즈 생성
    }

    @Transactional
    public void processArticle(Article article) {
        try {
            // 모든 정보를 한 번에 생성
            GeminiClient.ArticleAnalysis analysis = geminiClient.analyzeArticle(article.getContent());
            
            article.setTitle(analysis.getTitle());
            article.setSummary(analysis.getSummary());
            article.setExplanation(analysis.getExplanation());
            article.setTermExplanations(analysis.getTermExplanationsJson());
            
            articleRepository.save(article);
        } catch (Exception e) {
            log.error("기사 처리 중 오류 발생: {}", e.getMessage(), e);
            article.setSummary("요약 생성 실패");
            article.setExplanation("설명 생성 실패");
            articleRepository.save(article);
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
