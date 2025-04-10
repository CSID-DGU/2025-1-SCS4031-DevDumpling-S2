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
            "http://www.hani.co.kr/rss/economy/"
    );

    // 금융 관련 키워드 필터
    private static final List<String> FINANCE_KEYWORDS = Arrays.asList(
        "금융", "투자", "은행", "ETF", "코스피", "코스닥", "주식", "채권",
        "펀드", "증권", "금리", "환율", "자산", "저축", "보험", "대출"
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

            for (String rssUrl : RSS_URLS) {
                log.info("[RSS 스컨셔드러] RSS URL 처리 시작: {}", rssUrl);
                Elements items = fetchItemsWithJsoup(rssUrl);
                log.info("[RSS 스컨셔드러] 추출된 item 수: {}", items.size());

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
                            String content = rssArticleService.fetchContentFromUrl(link);
                            if (content == null || content.trim().isEmpty()) {
                                log.warn("[RSS 스케줄러] 기사 내용을 가져올 수 없음: {}", link);
                                continue;
                            }

                            Article article = Article.builder()
                                    .title(title)
                                    .content(content)
                                    .sourceUrl(link)
                                    .publishDate(parsePublishDate(pubDate))
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(pubDate, formatter);
            return zonedDateTime.toLocalDateTime();
        } catch (Exception e) {
            log.warn("[RSS 스컨셔드러] 날짜 파싱 실패: {} - {}", pubDate, e.getMessage());
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
