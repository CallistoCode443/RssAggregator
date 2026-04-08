package com.example.rssaggregator.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.rssaggregator.entity.Article;
import com.example.rssaggregator.entity.Source;
import com.example.rssaggregator.metrics.RssMetrics;
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
    private final RssMetrics rssMetrics;

    @Scheduled(cron = "${scheduling.rss-fetch-cron}")
    public void fetchAllSources() {
        List<Source> sources = sourceService.getActiveSources();
        log.info("Starting RSS fetch for {} sources", sources.size());

        for (Source source : sources) {
            rssMetrics.recordFetchDuration(() -> {
                try {
                    List<Article> articles = rssParser.parse(source);
                    articleService.saveNewArticles(source, articles);
                    log.info("Fetched {} articles from '{}'", articles.size(), source.getName());
                } catch (Exception e) {
                    rssMetrics.incrementFetchErrors();
                    log.error("Failed to fetch source '{}': {}", source.getName(), e.getMessage(), e);
                }
            });
        }
    }
}
