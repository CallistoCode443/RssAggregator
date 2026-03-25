package com.example.rssaggregator.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.rssaggregator.entity.Article;
import com.example.rssaggregator.entity.Source;
import com.example.rssaggregator.mapper.ArticleMapper;
import com.example.rssaggregator.metrics.RssMetrics;
import com.example.rssaggregator.model.ArticleDto;
import com.example.rssaggregator.model.ArticlePage;
import com.example.rssaggregator.repository.ArticleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;
    private final RssMetrics rssMetrics;

    public ArticlePage getArticles(String category, Long sourceId, String q, OffsetDateTime from, OffsetDateTime to,
            Pageable pageable) {
        Page<Article> page = articleRepository.findWithFilters(
                category,
                sourceId,
                q != null ? q.toLowerCase() : null,
                from != null ? from.atZoneSameInstant(ZoneOffset.UTC).toInstant() : null,
                to != null ? to.atZoneSameInstant(ZoneOffset.UTC).toInstant() : null,
                pageable);

        List<ArticleDto> content = page.getContent().stream()
                .map(articleMapper::toDto)
                .toList();

        ArticlePage result = new ArticlePage();
        result.setContent(content);
        result.setTotalElements(page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        result.setSize(page.getSize());
        result.setNumber(page.getNumber());
        return result;
    }

    @Transactional
    public void saveNewArticles(Source source, List<Article> articles) {
        if (articles.isEmpty()) {
            return;
        }

        Set<String> incomingUrls = articles.stream()
                .map(Article::getUrl)
                .collect(Collectors.toSet());

        Set<String> existingUrls = articleRepository.findExistingUrls(incomingUrls);

        List<Article> newArticles = articles.stream()
                .filter(a -> !existingUrls.contains(a.getUrl()))
                .toList();

        if (!newArticles.isEmpty()) {
            articleRepository.saveAll(newArticles);
            log.info("Saved {} new articles for source '{}'", newArticles.size(), source.getName());
        }

        rssMetrics.incrementArticlesSaved(newArticles.size());
    }
}
