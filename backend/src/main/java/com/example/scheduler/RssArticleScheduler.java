package com.example.scheduler;

import com.example.entity.Article;
import com.example.service.RssArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class RssArticleScheduler {

    private final RssArticleService rssArticleService;

    private static final List<String> RSS_URLS = Arrays.asList(
            "https://rss.etnews.com/Section901.xml",
            "http://www.hani.co.kr/rss/economy/",
            "https://www.hankyung.com/feed/finance"
    );

    // 금융 관련 키워드 필터
    private static final List<String> FINANCE_KEYWORDS = Arrays.asList(
        "투자"//, "금융", "은행", "ETF", "코스피", "코스닥", "주식", 
        //"펀드", "환율", "저축", "대출", "금융감독원", "코인", "나스닥"
    );

    private boolean containsFinanceKeyword(String title) {
        if (title == null) return false;
        String lowerTitle = title.toLowerCase();
        boolean contains = FINANCE_KEYWORDS.stream()
                .anyMatch(keyword -> lowerTitle.contains(keyword.toLowerCase()));
        
        if (contains) {
            log.info("[RSS 스컨셔드러] 금융 키워드 포함 기사 발견: {}", title);
        }
        return contains;
    }

    @Scheduled(cron = "0 0 9 * * ?", zone = "Asia/Seoul")
    public void fetchRssArticlesDaily() {
        try {
            int totalSaved = 0;
            int filteredCount = 0;
            int duplicateCount = 0;
            int saveFailedCount = 0;

            log.info("[RSS 스케줄러] 처리할 RSS URL 목록: {}", RSS_URLS);

            for (String rssUrl : RSS_URLS) {
                log.info("[RSS 스케줄러] RSS URL 처리 시작: {}", rssUrl);
                try {
                    Elements items = fetchItemsWithJsoup(rssUrl);
                    log.info("[RSS 스케줄러] 추출된 item 수: {}", items.size());

                    if (items.isEmpty()) {
                        log.warn("[RSS 스케줄러] {}에서 아이템을 추출하지 못했습니다.", rssUrl);
                        continue;
                    }

                    for (Element item : items) {
                        String title = getElementContent(item, "title");
                        String link = getElementContent(item, "link");
                        String pubDate = getElementContent(item, "pubDate");

                        if (title == null || link == null) {
                            log.warn("[RSS 스컨셔드러] 필수 데이터 누락 - 제목: {}, 링크: {}", title, link);
                            continue;
                        }

                        if (rssArticleService.existsByTitle(title)) {
                            duplicateCount++;
                            log.info("[RSS 스컨셔드러] 중복 기사 발견: {}", title);
                            continue;
                        }

                        if (containsFinanceKeyword(title)) {
                            try {
                                // 기사 내용 크롤링
                                String content = fetchContentFromUrl(link);
                                if (content == null || content.trim().isEmpty()) {
                                    log.warn("[RSS 스케줄러] 기사 내용을 가져올 수 없음: {}", link);
                                    continue;
                                }

                                Article article = Article.builder()
                                        .title(title)
                                        .content(content)
                                        .sourceUrl(link)
                                        .publishDate(parsePublishDate(pubDate))
                                        .status(Article.ProcessingStatus.PENDING)
                                        .explanation("처리 중...")
                                        .summary("처리 중...")
                                        .termExplanations("[]")
                                        .build();

                                if (!rssArticleService.existsByTitle(article.getTitle())) {
                                    log.info("[스케줄러] 새로운 기사 저장: {}", article.getTitle());
                                    rssArticleService.save(article);
                                    totalSaved++;
                                } else {
                                    log.info("[스케줄러] 이미 존재하는 기사: {}", article.getTitle());
                                }
                            } catch (Exception e) {
                                saveFailedCount++;
                                log.error("[RSS 스컨셔드러] 기사 저장 중 예외 발생: {} - {}", title, e.getMessage());
                            }
                        } else {
                            filteredCount++;
                        }
                    }
                } catch (Exception e) {
                    log.error("[RSS 스컨셔드러] RSS URL 처리 중 예외 발생: {} - {}", rssUrl, e.getMessage());
                }
            }

            log.info("[RSS 스컨셔드러] 총 저장된 기사 수: {}", totalSaved);
            log.info("[RSS 스컨셔드러] 필터링된 기사 수: {}", filteredCount);
            log.info("[RSS 스컨셔드러] 중복된 기사 수: {}", duplicateCount);
            log.info("[RSS 스컨셔드러] 저장 실패한 기사 수: {}", saveFailedCount);

        } catch (Exception e) {
            log.error("[RSS 스컨셔드러] 기사 수집 실패", e);
        }
    }

    private Elements fetchItemsWithJsoup(String rssUrl) {
        try {
            Document doc = Jsoup.connect(rssUrl).timeout(10_000).get();
            return doc.select("item");
        } catch (Exception e) {
            log.error("[RSS 스컨셔드러] Jsoup RSS 파싱 실패 - URL: {}", rssUrl, e);
            return new Elements();
        }
    }

    private String getElementContent(Element item, String tagName) {
        Element element = item.selectFirst(tagName);
        return element != null ? element.text().trim() : null;
    }

    private LocalDateTime parsePublishDate(String pubDate) {
        if (pubDate == null) {
            log.warn("[RSS 스컨셔드러] 날짜가 null입니다.");
            return LocalDateTime.now();
        }
        try {
            // 한국경제 RSS의 날짜 형식 처리
            if (pubDate.contains("+0900")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(pubDate, formatter);
                return zonedDateTime.toLocalDateTime();
            } else {
                // 다른 형식의 날짜 처리
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH);
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(pubDate, formatter);
                return zonedDateTime.toLocalDateTime();
            }
        } catch (Exception e) {
            log.warn("[RSS 스컨셔드러] 날짜 파싱 실패: {} - {}", pubDate, e.getMessage());
            return LocalDateTime.now();
        }
    }

    private String fetchContentFromUrl(String url) {
        try {
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
            } else if (url.contains("hankyung.com")) {
                // 한국경제 기사 본문
                contentElements = doc.select("div.article-body");
                if (contentElements.isEmpty()) {
                    contentElements = doc.select("div.article-content");
                }
            } else {
                contentElements = new Elements();
            }

            if (contentElements.isEmpty()) {
                log.warn("[RSS 스컨셔드러] 본문 요소를 찾을 수 없음: {}", url);
                return null;
            }

            String content = contentElements.text();
            content = cleanContent(content);
            
            if (content.isBlank()) {
                log.warn("[RSS 스컨셔드러] 본문이 비어있음: {}", url);
                return null;
            }

            return content;
        } catch (Exception e) {
            log.error("[RSS 스컨셔드러] 본문 크롤링 실패: {} - {}", url, e.getMessage());
            return null;
        }
    }

    private String cleanContent(String content) {
        if (content == null) return null;
        return content
                .replaceAll("기사를 읽어드립니다.*?audio element\\.", "")
                .replaceAll("\\[.*?\\]", "")
                .replaceAll("▶.*", "")
                .replaceAll("©.*", "")
                .replaceAll("\\s+", " ")
                .replaceAll("^\\s+|\\s+$", "")
                .trim();
    }
}
