package com.example.rssaggregator.metrics;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RssMetrics {
    private final MeterRegistry registry;
    private Counter articlesSavedCounter;
    private Counter fetchErrorsCounter;
    private Timer fetchDurationTimer;

    @PostConstruct
    public void init() {
        articlesSavedCounter = Counter.builder("rss.articles.saved")
                .description("Количество сохранённых статей")
                .register(registry);

        fetchErrorsCounter = Counter.builder("rss.fetch.errors")
                .description("Количество ошибок при обходе источников")
                .register(registry);

        fetchDurationTimer = Timer.builder("rss.fetch.duration")
                .description("Время обхода одного источника")
                .register(registry);
    }

    public void incrementArticlesSaved(int count) {
        articlesSavedCounter.increment(count);
    }

    public void incrementFetchErrors() {
        fetchErrorsCounter.increment();
    }

    public void recordFetchDuration(Runnable task) {
        fetchDurationTimer.record(task);
    }
}