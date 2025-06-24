package com.sunlife.demo.cachingdemo.util;

import com.sunlife.demo.cachingdemo.model.MetricRecord;
import com.sunlife.demo.cachingdemo.repository.MetricRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetricCollector {
    
    private final MetricRecordRepository metricRecordRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public void recordMetric(String cacheKey, 
                           MetricRecord.CacheType cacheType,
                           MetricRecord.CacheSource source,
                           long durationMs,
                           LocalDateTime ttlExpiresAt) {
        
        MetricRecord record = new MetricRecord();
        record.setCacheKey(cacheKey);
        record.setCacheType(cacheType);
        record.setSource(source);
        record.setDurationMs(durationMs);
        record.setTtlExpiresAt(ttlExpiresAt);
        
        metricRecordRepository.save(record);
        
        logMetric(cacheKey, cacheType, source, durationMs, ttlExpiresAt);
    }
    
    private void logMetric(String cacheKey, 
                          MetricRecord.CacheType cacheType,
                          MetricRecord.CacheSource source,
                          long durationMs,
                          LocalDateTime ttlExpiresAt) {
        
        String ttlInfo = ttlExpiresAt != null ? 
            " | TTL expires at: " + ttlExpiresAt.format(FORMATTER) : "";
        
        switch (source) {
            case CACHE_HIT -> {
                String message = String.format("[CACHE HIT - %s] Key: %s | Fetched in %dms%s", 
                    cacheType.name(), cacheKey, durationMs, ttlInfo);
                
                switch (cacheType) {
                    case REDIS -> log.info(AnsiColors.redis(AnsiColors.cacheHit(message)));
                    case MEMCACHED -> log.info(AnsiColors.memcached(AnsiColors.cacheHit(message)));
                    case HAZELCAST -> log.info(AnsiColors.hazelcast(AnsiColors.cacheHit(message)));
                }
            }
            case CACHE_MISS -> {
                String message = String.format("[CACHE MISS - %s] Key: %s", 
                    cacheType.name(), cacheKey);
                
                switch (cacheType) {
                    case REDIS -> log.info(AnsiColors.redis(AnsiColors.cacheMiss(message)));
                    case MEMCACHED -> log.info(AnsiColors.memcached(AnsiColors.cacheMiss(message)));
                    case HAZELCAST -> log.info(AnsiColors.hazelcast(AnsiColors.cacheMiss(message)));
                }
            }
            case DB_FETCH -> {
                String message = String.format("[DB FETCH] Key: %s | Took %dms%s", 
                    cacheKey, durationMs, ttlInfo);
                log.info(AnsiColors.dbFetch(message));
            }
        }
    }
}
