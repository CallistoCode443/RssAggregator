package com.example.rssaggregator.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.rssaggregator.entity.Source;
import com.example.rssaggregator.mapper.SourceMapper;
import com.example.rssaggregator.model.CreateSourceRequest;
import com.example.rssaggregator.model.SourceDto;
import com.example.rssaggregator.repository.SourceRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SourceService {
    private final SourceRepository sourceRepository;
    private final SourceMapper sourceMapper;

    @Transactional
    public SourceDto createSource(CreateSourceRequest request) {
        Source source = Source.builder()
                .name(request.getName())
                .url(request.getUrl())
                .category(request.getCategory())
                .active(true)
                .build();
        return sourceMapper.toDto(sourceRepository.save(source));
    }

    public List<SourceDto> getAllSources() {
        return sourceRepository.findAll().stream()
                .map(sourceMapper::toDto)
                .toList();
    }

    @Transactional
    public SourceDto toggleSource(Long id) {
        Source source = sourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Source not found with id: " + id));
        source.setActive(!source.getActive());
        return sourceMapper.toDto(sourceRepository.save(source));
    }

    @Transactional
    public void deleteSource(Long id) {
        if (!sourceRepository.existsById(id)) {
            throw new EntityNotFoundException("Source not found with id: " + id);
        }
        sourceRepository.deleteById(id);
    }

    public List<Source> getActiveSources() {
        return sourceRepository.findAllByActiveTrue();
    }
}
