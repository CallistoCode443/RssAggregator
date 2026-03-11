package com.example.rssaggregator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rssaggregator.entity.Source;

@Repository
public interface SourceRepository extends JpaRepository<Source, Long>{
    List<Source> findAllByActiveTrue();
}
