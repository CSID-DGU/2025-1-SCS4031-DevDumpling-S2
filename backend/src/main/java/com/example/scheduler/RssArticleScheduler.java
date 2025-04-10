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
            "https://www.mk.co.kr/rss/30000001/",
            "http://www.hani.co.kr/rss/economy/"
    );

    // 금융 관련 키워드 필터
    private static final List<String> FINANCE_KEYWORDS = Arrays.asList(
        "금융", "투자", "은행", "ETF", "코스피", "코스닥", "주식", "채권",
        "펀드", "증권", "금리", "환율", "자산", "저축", "보험", "대출"
    );

    @Scheduled(cron = "0 0 9 * * ?", zone = "Asia/Seoul")
    public void fetchRssArticlesDaily() {
        try {
            int totalSaved = 0;

            for (String rssUrl : RSS_URLS) {
                log.info("[RSS 스컨셔드러] RSS URL 처리 시작: {}", rssUrl);
                Elements items = fetchItemsWithJsoup(rssUrl);
                log.info("[RSS 스컨셔드러] 추출된 item 수: {}", items.size());

                for (Element item : items) {
                    String title = getElementContent(item, "title");
                    String link = getElementContent(item, "link");
                    String pubDate = getElementContent(item, "pubDate");

                    if (!rssArticleService.existsByTitle(title)) {
                        Article article = Article.builder()
                                .title(title)
                                .sourceUrl(link)
                                .publishDate(parsePublishDate(pubDate))
                                .build();

                        Article saved = rssArticleService.save(article);
                        if (saved != null) totalSaved++;
                    }
                }
            }

            log.info("[RSS 스컨셔드러] 총 저장된 기사 수: {}", totalSaved);

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
        if (pubDate == null) return LocalDateTime.now();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(pubDate, formatter);
            return zonedDateTime.toLocalDateTime();
        } catch (Exception e) {
            log.warn("[RSS 스컨셔드러] 날짜 파싱 실패: {}", pubDate);
            return LocalDateTime.now();
        }
    }
    /**
    private LocalDateTime parsePublishDate(String pubDate) {
        try {
            if (pubDate == null) return LocalDateTime.now();
            
            // 네이버 뉴스 RSS의 날짜 형식에 맞춰 파싱
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
            Date date = sdf.parse(pubDate);
            return date.toInstant()
                    .atZone(ZoneId.of("Asia/Seoul"))
                    .toLocalDateTime();
        } catch (Exception e) {
            log.warn("[RSS 스케줄러] 날짜 파싱 실패: {} - {}", pubDate, e.getMessage());
            return LocalDateTime.now();
        }
    }
    **/
}
