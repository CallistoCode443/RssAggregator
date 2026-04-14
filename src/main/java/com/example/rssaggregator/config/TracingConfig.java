package com.example.rssaggregator.config;

import brave.Tracing;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.brave.bridge.BraveCurrentTraceContext;
import io.micrometer.tracing.brave.bridge.BraveTracer;
import io.micrometer.tracing.brave.bridge.BraveBaggageManager;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.brave.ZipkinSpanHandler;
import zipkin2.reporter.okhttp3.OkHttpSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {

    @Bean
    public Sender sender() {
        return OkHttpSender.create("http://localhost:9411/api/v2/spans");
    }

    @Bean
    public AsyncReporter<zipkin2.Span> spanReporter(Sender sender) {
        return AsyncReporter.create(sender);
    }

    @Bean
    public Tracing tracing(AsyncReporter<zipkin2.Span> spanReporter) {
        return Tracing.newBuilder()
                .localServiceName("rssaggregator")
                .addSpanHandler(ZipkinSpanHandler.create(spanReporter)) // не spanReporter()
                .build();
    }

    @Bean
    public brave.Tracer braveTracer(Tracing tracing) {
        return tracing.tracer();
    }

    @Bean
    public Tracer tracer(brave.Tracer braveTracer, Tracing tracing) {
        return new BraveTracer(
                braveTracer,
                new BraveCurrentTraceContext(tracing.currentTraceContext()),
                new BraveBaggageManager());
    }
}
