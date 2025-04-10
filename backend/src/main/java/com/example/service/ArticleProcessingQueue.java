package com.example.service;

import com.example.entity.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleProcessingQueue {

    private final RssArticleService rssArticleService;
    private final BlockingQueue<Article> queue = new LinkedBlockingQueue<>();
    private volatile boolean isProcessing = false;

    public void addArticle(Article article) {
        try {
            queue.put(article);
            if (!isProcessing) {
                startProcessing();
            }
        } catch (InterruptedException e) {
            log.error("[큐 서비스] 기사 추가 실패: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Async
    public void startProcessing() {
        isProcessing = true;
        try {
            while (!queue.isEmpty()) {
                Article article = queue.take();
                try {
                    // API 호출 전 3초 대기 (rate limit 방지)
                    Thread.sleep(3000);
                    rssArticleService.processArticle(article);
                } catch (Exception e) {
                    log.error("[큐 서비스] 기사 처리 실패: {} - {}", article.getTitle(), e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            log.error("[큐 서비스] 처리 중단: {}", e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            isProcessing = false;
        }
    }

    public int getQueueSize() {
        return queue.size();
    }
} 