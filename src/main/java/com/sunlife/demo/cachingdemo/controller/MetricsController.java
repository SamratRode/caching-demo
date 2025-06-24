package com.sunlife.demo.cachingdemo.controller;

import com.sunlife.demo.cachingdemo.dto.MetricResponse;
import com.sunlife.demo.cachingdemo.model.MetricRecord;
import com.sunlife.demo.cachingdemo.repository.MetricRecordRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Metrics", description = "Endpoints for viewing cache performance metrics")
public class MetricsController {
    
    private final MetricRecordRepository metricRecordRepository;
    
    @GetMapping("/metrics")
    @Operation(
        summary = "Get cache metrics", 
        description = "Retrieve performance metrics for all cache operations"
    )
    @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully")
    public ResponseEntity<List<MetricResponse>> getMetrics(
            @RequestParam(defaultValue = "50") int limit) {
        
        log.info(" Fetching cache metrics with limit: {}", limit);
        
        List<MetricRecord> records = metricRecordRepository.findTopNOrderByCreatedAtDesc(limit);
        
        List<MetricResponse> responses = records.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        log.info(" Retrieved {} metric records", responses.size());
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/metrics/{cacheType}")
    @Operation(
        summary = "Get metrics by cache type", 
        description = "Retrieve performance metrics for a specific cache type"
    )
    @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid cache type")
    public ResponseEntity<List<MetricResponse>> getMetricsByCacheType(
            @PathVariable String cacheType) {
        
        log.info(" Fetching metrics for cache type: {}", cacheType);
        
        try {
            MetricRecord.CacheType type = MetricRecord.CacheType.valueOf(cacheType.toUpperCase());
            
            List<MetricRecord> records = metricRecordRepository
                .findByCacheTypeOrderByCreatedAtDesc(type);
            
            List<MetricResponse> responses = records.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            log.info(" Retrieved {} metric records for cache type: {}", responses.size(), cacheType);
            return ResponseEntity.ok(responses);
            
        } catch (IllegalArgumentException e) {
            log.warn(" Invalid cache type requested: {}", cacheType);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/metrics")
    @Operation(
        summary = "Clear all metrics", 
        description = "Delete all stored cache metrics"
    )
    @ApiResponse(responseCode = "204", description = "Metrics cleared successfully")
    public ResponseEntity<Void> clearMetrics() {
        log.info(" Clearing all cache metrics");
        
        metricRecordRepository.deleteAll();
        
        log.info(" All cache metrics cleared");
        return ResponseEntity.noContent().build();
    }
    
    private MetricResponse convertToResponse(MetricRecord record) {
        return new MetricResponse(
            record.getCacheKey(),
            record.getCacheType().name(),
            record.getSource().name(),
            record.getDurationMs(),
            record.getTtlExpiresAt(),
            record.getCreatedAt()
        );
    }
}
