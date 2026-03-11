package com.example.rssaggregator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rssaggregator.scheduler.RssFetchScheduler;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class DevController {

    private final RssFetchScheduler rssFetchScheduler;

    @PostMapping("/fetch")
    public ResponseEntity<Void> triggerFetch() {
        rssFetchScheduler.fetchAllSources();
        return ResponseEntity.accepted().build();
    }
}