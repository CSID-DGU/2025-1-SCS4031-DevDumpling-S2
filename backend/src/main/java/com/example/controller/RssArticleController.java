package com.example.controller;

import com.example.entity.Article;
import com.example.scheduler.RssArticleScheduler;
import com.example.service.RssArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rss")
@RequiredArgsConstructor
@Slf4j
public class RssArticleController {

    private final RssArticleScheduler rssArticleScheduler;
    private final RssArticleService rssArticleService;

    // 수동 RSS 수집
    @PostMapping("/fetch")
    public ResponseEntity<String> fetchRssManually() {
        log.info("[API] 수동 크롤링 호출 시작");
        rssArticleScheduler.fetchRssArticlesDaily();
        log.info("[API] 수동 크롤링 호출 완료");
        return ResponseEntity.ok("크롤링 요청이 완료되었습니다.");
    }

    // 헬스 체크
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("RSS API is alive");
    }

    // 테스트용 크롤링
    @GetMapping("/test-crawl")
    public ResponseEntity<String> testCrawling() {
        String testUrl = "https://n.news.naver.com/mnews/article/014/0005126671";
        String content = rssArticleService.testCrawling(testUrl);
        return ResponseEntity.ok(content);
    }

    // 기사 전체 리스트
    @GetMapping("/list")
    public ResponseEntity<List<Article>> listArticles() {
        List<Article> articles = rssArticleService.findAll();
        return ResponseEntity.ok(articles);
    }

    // 특정 기사 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticle(@PathVariable Long id) {
        return ResponseEntity.ok(rssArticleService.findById(id));
    }
}
