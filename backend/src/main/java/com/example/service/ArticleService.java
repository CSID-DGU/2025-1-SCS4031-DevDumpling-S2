package com.example.service;

import com.example.api.GeminiClient;
import com.example.entity.Article;
import com.example.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final GeminiClient geminiClient;

    @Transactional
    public void processArticles(List<Article> articles) {
        int successCount = 0;
        int failureCount = 0;
        
        for (Article article : articles) {
            try {
                processArticle(article);
                successCount++;
            } catch (Exception e) {
                failureCount++;
                log.error("기사 처리 실패 - 제목: {}, ID: {}", article.getTitle(), article.getId(), e);
            }
        }
        
        // 전체 처리 결과 로깅
        log.info("기사 처리 완료 - 총 처리: {}, 성공: {}, 실패: {}", 
            articles.size(), successCount, failureCount);
            
        if (failureCount > 0) {
            log.warn("실패한 기사 목록:");
            List<Article> failedArticles = articleRepository.findByStatus(Article.ProcessingStatus.FAILED);
            failedArticles.forEach(failedArticle -> 
                log.warn("실패한 기사 - ID: {}, 제목: {}", 
                    failedArticle.getId(), failedArticle.getTitle()));
        }
    }

    @Transactional
    public void processArticle(Article article) {
        try {
            // Gemini API 호출
            GeminiClient.ArticleAnalysis analysis = geminiClient.analyzeArticle(article.getContent());
            
            if (analysis == null) {
                throw new IllegalStateException("Gemini API 응답이 유효하지 않습니다.");
            }
            
            // 정상이면 업데이트
            article.setSummary(analysis.getSummary());
            article.setExplanation(analysis.getExplanation());
            article.setTermExplanations(analysis.getTermExplanationsJson());
            article.setStatus(Article.ProcessingStatus.COMPLETED);
            
            articleRepository.save(article);
            
        } catch (Exception e) {
            // 실패 시 상태만 업데이트
            article.setStatus(Article.ProcessingStatus.FAILED);
            articleRepository.save(article);
            throw e;
        }
    }
} 