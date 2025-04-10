package com.example.api;

import com.example.entity.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class RssNewsClient {

    public List<Article> fetchArticlesFromRss(String rssUrl) {
        List<Article> articles = new ArrayList<>();
        try {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(new URL(rssUrl).openStream());

            NodeList items = doc.getElementsByTagName("item");
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);

                String title = item.getElementsByTagName("title").item(0).getTextContent();
                String link = item.getElementsByTagName("link").item(0).getTextContent();
                String pubDate = item.getElementsByTagName("pubDate").item(0).getTextContent();

                Article article = Article.builder()
                        .title(title)
                        .sourceUrl(link)
                        .content(title)
                        .publishDate(LocalDateTime.now()) // pubDate 파싱은 필요시 확장 가능
                        .build();

                articles.add(article);
            }
        } catch (Exception e) {
            log.error("RSS 파싱 실패", e);
        }
        return articles;
    }
}