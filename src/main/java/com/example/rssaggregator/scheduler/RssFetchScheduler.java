package com.example.rssaggregator.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.rssaggregator.entity.Article;
import com.example.rssaggregator.entity.Source;
import com.example.rssaggregator.parser.RssParser;
import com.example.rssaggregator.service.ArticleService;
import com.example.rssaggregator.service.SourceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RssFetchScheduler {
    private final SourceService sourceService;
    private final ArticleService articleService;
    private final RssParser rssParser;

    @Scheduled(cron = "${scheduling.rss-fetch.cron}")
    public void fetchAllSources() {
        List<Source> activeSources = sourceService.getActivSources();
        log.info("Starting RSS fetch for {} active sources", activeSources.size());

        for (Source source : activeSources) {
            try {
                List<Article> articles = rssParser.parse(source);
                articleService.saveNewArticles(source, articles);
            } catch (Exception e) {
                log.error("Error fetching source '{}': {}", source.getName(), e.getMessage());
            }
        }

        log.info("RSS fetch completed");
    }
}
