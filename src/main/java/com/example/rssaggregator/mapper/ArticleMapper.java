package com.example.rssaggregator.mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.rssaggregator.entity.Article;
import com.example.rssaggregator.model.ArticleDto;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
    @Mapping(target = "sourceId", source = "source.id")
    @Mapping(target = "sourceName", source = "source.name")
    @Mapping(target = "publishedAt", expression = "java(toOffsetDateTime(article.getPublishedAt()))")
    @Mapping(target = "fetchedAt", expression = "java(toOffsetDateTime(article.getFetchedAt()))")
    ArticleDto toDto(Article article);

    default OffsetDateTime toOffsetDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }

        return instant.atOffset(ZoneOffset.UTC);
    }
}
