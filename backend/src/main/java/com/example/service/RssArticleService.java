package com.example.service;

import com.example.api.GeminiClient;
import com.example.entity.Article;
import com.example.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RssArticleService {

    private final ArticleRepository articleRepository;
    private final GeminiClient geminiClient;
    private final QuizService quizService;

    public boolean existsByTitle(String title) {
        return articleRepository.existsByTitle(title);
    }

    public Article save(Article article) {
        try {
            log.info("[기사 서비스] 저장 시작 - {}", article.getTitle());

            String content = fetchContentFromUrl(article.getSourceUrl());
            if (content == null || content.isBlank()) return null;
            article.setContent(content);

            try {
                String summary = geminiClient.summarize(content);
                article.setSummary(summary);
            } catch (Exception e) {
                article.setSummary("요약 실패");
            }

            try {
                String explanation = geminiClient.explainSimply(content);
                article.setExplanation(explanation);
            } catch (Exception e) {
                article.setExplanation("설명 실패");
            }

            Article saved = articleRepository.save(article);
            quizService.generateQuizzesForArticle(saved);
            return saved;

        } catch (Exception e) {
            log.error("기사 저장 실패", e);
            return null;
        }
    }

    public String fetchContentFromUrl(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(30000)
                    .get();

            Elements contentElements = doc.select("#dic_area, #articleBodyContents, #newsEndContents");
            if (contentElements.isEmpty()) return null;

            String content = contentElements.text();
            return cleanContent(content);
        } catch (Exception e) {
            log.error("본문 크롤링 실패", e);
            return null;
        }
    }

    private String cleanContent(String content) {
        return content.replaceAll("\\[.*?\\]", "")
                .replaceAll("▶.*", "")
                .replaceAll("©.*", "")
                .replaceAll("\\s+", " ")
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
