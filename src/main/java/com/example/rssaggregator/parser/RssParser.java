package com.example.rssaggregator.parser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.example.rssaggregator.entity.Article;
import com.example.rssaggregator.entity.Source;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RssParser {
    public List<Article> parse(Source source) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(source.getUrl()).openConnection();
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (compatible; RssAggregator/1.0)");
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(15_000);

            SyndFeedInput input = new SyndFeedInput();
            input.setAllowDoctypes(true);

            try (XmlReader reader = new XmlReader(connection.getInputStream())) {
                SyndFeed feed = input.build(reader);
                return feed.getEntries().stream()
                        .map(entry -> mapEntry(entry, source))
                        .filter(Objects::nonNull)
                        .toList();
            }
        } catch (Exception e) {
            log.error("Failed to parse RSS feed for source '{}' ({})",
                    source.getName(), source.getUrl(), e);
            return Collections.emptyList();
        }
    }

    private Article mapEntry(SyndEntry entry, Source source) {
        String url = entry.getLink();
        if (url == null || url.isBlank()) {
            return null;
        }

        String title = entry.getTitle() != null ? entry.getTitle().trim() : "No title";
        String description = null;
        if (entry.getDescription() != null) {
            description = entry.getDescription().getValue();
        } else if (!entry.getContents().isEmpty()) {
            description = entry.getContents().get(0).getValue();
        }

        Instant publishedAt = toInstant(
                entry.getPublishedDate() != null ? entry.getPublishedDate() : entry.getUpdatedDate());

        return Article.builder()
                .source(source)
                .title(title)
                .description(description)
                .url(url.trim())
                .publishedAt(publishedAt)
                .fetchedAt(Instant.now())
                .build();
    }

    private Instant toInstant(Date date) {
        if (date == null) {
            return null;
        }

        return date.toInstant();
    }
}
