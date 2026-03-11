package com.example.rssaggregator.controller;

import java.time.OffsetDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.rssaggregator.api.ArticlesApi;
import com.example.rssaggregator.model.ArticlePage;
import com.example.rssaggregator.service.ArticleService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;

@RestController
@RequiredArgsConstructor
public class ArticlesController implements ArticlesApi {

    private final ArticleService articleService;

    @Override
    public ResponseEntity<ArticlePage> getArticles(
        String category, 
        Long sourceId, 
        String q,
        OffsetDateTime from, 
        OffsetDateTime to, 
        Integer page, 
        Integer size,
        String sort) {

        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 20;

        PageRequest pageable = PageRequest.of(pageNum, pageSize);
        ArticlePage result = articleService.getArticles(category, sourceId, q, from, to, pageable);
        return ResponseEntity.ok(result);
    }
    
}
