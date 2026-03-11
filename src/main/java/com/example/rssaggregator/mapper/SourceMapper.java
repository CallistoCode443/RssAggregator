package com.example.rssaggregator.mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.rssaggregator.entity.Source;
import com.example.rssaggregator.model.SourceDto;

@Mapper(componentModel = "spring")
public interface SourceMapper {
    @Mapping(target = "createdAt", expression = "java(toOffsetDateTime(source.getCreatedAt()))")
    SourceDto toDto(Source source);

    default OffsetDateTime toOffsetDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }

        return instant.atOffset(ZoneOffset.UTC);
    }
}
