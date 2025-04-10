package com.example.api;

import com.example.entity.Article;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class RssNewsClient {

    public List<Article> fetchArticlesFromRss(String rssUrl) {
        List<Article> articles = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(rssUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                    .timeout(30000)
                    .get();

            Elements items = doc.select("item");
            for (Element item : items) {
                String title = item.select("title").text();
                String link = item.select("link").text();
                String pubDate = item.select("pubDate").text();

                // 기사 내용 가져오기
                String content = fetchContentFromUrl(link);
                if (content == null || content.trim().isEmpty()) {
                    log.warn("[RSS 클라이언트] 기사 내용을 가져올 수 없음: {}", link);
                    continue;
                }

                Article article = Article.builder()
                        .title(title)
                        .sourceUrl(link)
                        .content(content)
                        .publishDate(LocalDateTime.now()) // pubDate 파싱은 필요시 확장 가능
                        .build();

                articles.add(article);
            }
        } catch (Exception e) {
            log.error("RSS 파싱 실패", e);
        }
        return articles;
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
                contentElements = doc.select("#articleBody");
                if (contentElements.isEmpty()) {
                    contentElements = doc.select("#content");
                }
            } else if (url.contains("hani.co.kr")) {
                contentElements = doc.select("div.text");
                if (contentElements.isEmpty()) {
                    contentElements = doc.select("div.article-text");
                }
            } else if (url.contains("mk.co.kr")) {
                contentElements = doc.select("div#article_body");
            } else {
                contentElements = new Elements();
            }

            if (contentElements.isEmpty()) {
                log.warn("[RSS 클라이언트] 본문 요소를 찾을 수 없음: {}", url);
                return null;
            }

            String content = contentElements.text();
            content = cleanContent(content);
            
            if (content.isBlank()) {
                log.warn("[RSS 클라이언트] 본문이 비어있음: {}", url);
                return null;
            }

            return content;
        } catch (Exception e) {
            log.error("[RSS 클라이언트] 본문 크롤링 실패: {} - {}", url, e.getMessage());
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