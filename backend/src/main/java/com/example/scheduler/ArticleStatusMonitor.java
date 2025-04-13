package com.example.scheduler;

import com.example.entity.Article;
import com.example.repository.ArticleRepository;
import com.example.service.ArticleProcessingQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleStatusMonitor {

    private final ArticleRepository articleRepository;
    private final ArticleProcessingQueue processingQueue;

    @Scheduled(fixedRate = 300000) // 5분마다 실행
    public void monitorArticleStatus() {
        log.info("[모니터] 기사 상태 모니터링 시작");
        
        // 상태별 기사 수 조회
        long pendingCount = articleRepository.countPending();
        long completedCount = articleRepository.countCompleted();
        long failedCount = articleRepository.countFailed();
        
        log.info("[모니터] 기사 상태 현황 - PENDING: {}, COMPLETED: {}, FAILED: {}", 
            pendingCount, completedCount, failedCount);
        
        // 30분 이상 PENDING 상태인 기사 확인
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minus(30, ChronoUnit.MINUTES);
        List<Article> pendingArticles = articleRepository.findPending();
        
        for (Article article : pendingArticles) {
            if (article.getPublishDate().isBefore(thirtyMinutesAgo)) {
                log.warn("[모니터] 장시간 PENDING 상태인 기사 발견: {} (ID: {}, 발행일: {})", 
                    article.getTitle(), article.getId(), article.getPublishDate());
                // 큐에 다시 추가
                processingQueue.addArticle(article);
            }
        }

        // COMPLETED 상태이지만 불완전한 기사 확인
        List<Article> completedArticles = articleRepository.findCompleted();
        for (Article article : completedArticles) {
            if (isIncompleteArticle(article)) {
                log.warn("[모니터] 불완전한 기사 발견: {} (ID: {})", 
                    article.getTitle(), article.getId());
                // 상태를 PENDING으로 변경하고 큐에 다시 추가
                article.setStatus(Article.ProcessingStatus.PENDING);
                articleRepository.save(article);
                processingQueue.addArticle(article);
            }
        }
        
        log.info("[모니터] 기사 상태 모니터링 완료");
    }

    private boolean isIncompleteArticle(Article article) {
        return isInvalidField(article.getExplanation()) || 
               isInvalidField(article.getSummary()) || 
               isInvalidField(article.getTermExplanations());
    }

    private boolean isInvalidField(String field) {
        return field == null || 
               field.isEmpty() || 
               field.equals("처리 중...") || 
               field.equals("[]") || 
               field.equals("기사 처리에 실패했습니다.");
    }
} 