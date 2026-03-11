package com.example.rssaggregator.repository;

import java.time.Instant;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.rssaggregator.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Query(value = """
            SELECT a.*
            FROM articles a
            JOIN sources s ON s.id = a.source_id
            WHERE (:category IS NULL OR s.category = :category)
            AND (:sourceId IS NULL OR s.id = :sourceId)
            AND (:q IS NULL OR lower(a.title) LIKE lower('%' || CAST(:q AS text) || '%'))
            ORDER BY a.published_at DESC
            """, 
            countQuery = """
            SELECT COUNT(*)
            FROM articles a
            JOIN sources s ON s.id = a.source_id
            WHERE (:category IS NULL OR s.category = :category)
            AND (:sourceId IS NULL OR s.id = :sourceId)
            AND (:q IS NULL OR lower(a.title) LIKE lower('%' || CAST(:q AS text) || '%'))
            """, nativeQuery = true)
    Page<Article> findWithFilters(
            @Param("category") String category,
            @Param("sourceId") Long sourceId,
            @Param("q") String q,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable);

    @Query("SELECT a.url FROM Article a WHERE a.url IN :urls")
    Set<String> findExistingUrls(@Param("urls") Set<String> urls);
}
