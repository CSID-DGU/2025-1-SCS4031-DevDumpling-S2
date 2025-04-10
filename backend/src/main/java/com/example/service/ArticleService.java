package com.example.service;

import com.example.api.GeminiClient;
import com.example.entity.Article;
import com.example.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private GeminiClient geminiClient;

    @Transactional
    public Article processArticle(Article article) {
        String content = article.getContent();
        
        // 모든 정보를 한 번에 생성
        GeminiClient.ArticleAnalysis analysis = geminiClient.analyzeArticle(content);
        
        article.setTitle(analysis.getTitle());
        article.setSummary(analysis.getSummary());
        article.setExplanation(analysis.getExplanation());
        article.setTermExplanations(analysis.getTermExplanationsJson());
        
        return articleRepository.save(article);
    }
} 