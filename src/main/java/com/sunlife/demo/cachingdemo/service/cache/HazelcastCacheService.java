package com.sunlife.demo.cachingdemo.service.cache;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.sunlife.demo.cachingdemo.model.CachedData;
import com.sunlife.demo.cachingdemo.model.MetricRecord;
import com.sunlife.demo.cachingdemo.service.DataService;
import com.sunlife.demo.cachingdemo.util.MetricCollector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class HazelcastCacheService {
    
    private final HazelcastInstance hazelcastInstance;
    private final DataService dataService;
    private final MetricCollector metricCollector;
    
    @Value("${cache.ttl-minutes}")
    private int ttlMinutes;
    
    private static final String CACHE_MAP_NAME = "cache-data";
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> getValue(String key) {
        long startTime = System.currentTimeMillis();
        
        IMap<String, Object> cache = hazelcastInstance.getMap(CACHE_MAP_NAME);
        
        // Try to get from Hazelcast cache first
        Object cachedValue = cache.get(key);
        
        if (cachedValue != null) {
            long duration = System.currentTimeMillis() - startTime;
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(ttlMinutes);
            
            metricCollector.recordMetric(key, MetricRecord.CacheType.HAZELCAST, 
                MetricRecord.CacheSource.CACHE_HIT, duration, expiresAt);
            
            return (Map<String, Object>) cachedValue;
        }
        
        // Cache miss - record it
        metricCollector.recordMetric(key, MetricRecord.CacheType.HAZELCAST, 
            MetricRecord.CacheSource.CACHE_MISS, 0L, null);
        
        // Fetch from database
        long dbStartTime = System.currentTimeMillis();
        Optional<CachedData> dbResult = dataService.findByKey(key);
        
        if (dbResult.isPresent()) {
            Map<String, Object> value = dbResult.get().getCacheValue();
            long dbDuration = System.currentTimeMillis() - dbStartTime;
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(ttlMinutes);
            
            // Cache the result in Hazelcast with TTL
            cache.put(key, value, ttlMinutes, TimeUnit.MINUTES);
            
            metricCollector.recordMetric(key, MetricRecord.CacheType.HAZELCAST, 
                MetricRecord.CacheSource.DB_FETCH, dbDuration, expiresAt);
            
            return value;
        }
        
        return null;
    }
    
    public void setValue(String key, Map<String, Object> value) {
        // Save to database first
        dataService.saveData(key, value);
        
        // Then cache in Hazelcast with TTL
        IMap<String, Object> cache = hazelcastInstance.getMap(CACHE_MAP_NAME);
        cache.put(key, value, ttlMinutes, TimeUnit.MINUTES);
        
        log.info("⚡ Cached data in Hazelcast for key: {} with TTL: {} minutes", key, ttlMinutes);
    }
    
    public void evict(String key) {
        IMap<String, Object> cache = hazelcastInstance.getMap(CACHE_MAP_NAME);
        cache.remove(key);
        log.info("🗑️ Evicted key from Hazelcast cache: {}", key);
    }
    
    public boolean exists(String key) {
        IMap<String, Object> cache = hazelcastInstance.getMap(CACHE_MAP_NAME);
        return cache.containsKey(key);
    }
    
    public Map<String, Object> getCacheStats() {
        IMap<String, Object> cache = hazelcastInstance.getMap(CACHE_MAP_NAME);
        return Map.of(
            "size", cache.size(),
            "localMapStats", cache.getLocalMapStats()
        );
    }
}
