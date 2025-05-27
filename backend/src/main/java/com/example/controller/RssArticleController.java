package com.example.controller;

import com.example.entity.Article;
import com.example.entity.User;
import com.example.scheduler.RssArticleScheduler;
import com.example.service.RssArticleService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rss")
@RequiredArgsConstructor
@Slf4j
public class RssArticleController {

    private final RssArticleScheduler rssArticleScheduler;
    private final RssArticleService rssArticleService;
    private final UserService userService;

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
    @Transactional(readOnly = true)
    public ResponseEntity<Article> getArticle(@PathVariable Long id) {
        Article article = rssArticleService.findById(id);
        // Lazy Loading 컬렉션을 명시적으로 초기화
        article.getScrappedBy().size();
        return ResponseEntity.ok(article);
    }

    // 기사 스크랩
    @PostMapping("/{articleId}/scrap")
    @Transactional
    public ResponseEntity<?> scrapArticle(
            @PathVariable Long articleId,
            Authentication authentication) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            rssArticleService.scrapArticle(user, articleId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("[스크랩] 기사 스크랩 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 기사 스크랩 취소
    @DeleteMapping("/{articleId}/scrap")
    @Transactional
    public ResponseEntity<?> unscrapArticle(
            @PathVariable Long articleId,
            Authentication authentication) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            rssArticleService.unscrapArticle(user, articleId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("[스크랩] 기사 스크랩 취소 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 사용자의 스크랩한 기사 목록 조회
    @GetMapping("/scrapped")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Article>> getScrappedArticles(Authentication authentication) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            List<Article> scrappedArticles = rssArticleService.getScrappedArticles(user);
            return ResponseEntity.ok(scrappedArticles);
        } catch (Exception e) {
            log.error("[스크랩] 스크랩한 기사 목록 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
