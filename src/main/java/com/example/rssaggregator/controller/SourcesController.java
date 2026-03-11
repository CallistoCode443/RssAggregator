package com.example.rssaggregator.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.rssaggregator.api.SourcesApi;
import com.example.rssaggregator.model.CreateSourceRequest;
import com.example.rssaggregator.model.SourceDto;
import com.example.rssaggregator.service.SourceService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SourcesController implements SourcesApi {
    private final SourceService sourceService;
    @Override
    public ResponseEntity<SourceDto> createSource(@Valid CreateSourceRequest createSourceRequest) {
        SourceDto created = sourceService.createSource(createSourceRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<Void> deleteSource(@NotNull Long id) {
        sourceService.deleteSource(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<SourceDto>> getSources() {
        return ResponseEntity.ok(sourceService.getAllSources());
    }

    @Override
    public ResponseEntity<SourceDto> toggleSource(@NotNull Long id) {
        return ResponseEntity.ok(sourceService.toggleSource(id));
    }
}
