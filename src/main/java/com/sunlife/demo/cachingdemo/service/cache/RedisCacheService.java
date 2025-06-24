package com.sunlife.demo.cachingdemo.service.cache;

import com.sunlife.demo.cachingdemo.model.CachedData;
import com.sunlife.demo.cachingdemo.model.MetricRecord;
import com.sunlife.demo.cachingdemo.service.DataService;
import com.sunlife.demo.cachingdemo.util.MetricCollector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final DataService dataService;
    private final MetricCollector metricCollector;
    
    @Value("${cache.ttl-minutes}")
    private int ttlMinutes;
    
    public Map<String, Object> getValue(String key) {
        long startTime = System.currentTimeMillis();
        
        // Try to get from Redis cache first
        Object cachedValue = redisTemplate.opsForValue().get(key);
        
        if (cachedValue != null) {
            long duration = System.currentTimeMillis() - startTime;
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(ttlMinutes);
            
            metricCollector.recordMetric(key, MetricRecord.CacheType.REDIS, 
                MetricRecord.CacheSource.CACHE_HIT, duration, expiresAt);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) cachedValue;
            return result;
        }
        
        // Cache miss - record it
        metricCollector.recordMetric(key, MetricRecord.CacheType.REDIS, 
            MetricRecord.CacheSource.CACHE_MISS, 0L, null);
        
        // Fetch from database
        long dbStartTime = System.currentTimeMillis();
        Optional<CachedData> dbResult = dataService.findByKey(key);
        
        if (dbResult.isPresent()) {
            Map<String, Object> value = dbResult.get().getCacheValue();
            long dbDuration = System.currentTimeMillis() - dbStartTime;
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(ttlMinutes);
            
            // Cache the result in Redis
            redisTemplate.opsForValue().set(key, value, ttlMinutes, TimeUnit.MINUTES);
            
            metricCollector.recordMetric(key, MetricRecord.CacheType.REDIS, 
                MetricRecord.CacheSource.DB_FETCH, dbDuration, expiresAt);
            
            return value;
        }
        
        return null;
    }
    
    public void setValue(String key, Map<String, Object> value) {
        // Save to database first
        dataService.saveData(key, value);
        
        // Then cache in Redis
        redisTemplate.opsForValue().set(key, value, ttlMinutes, TimeUnit.MINUTES);
        
        log.info(" Cached data in Redis for key: {} with TTL: {} minutes", key, ttlMinutes);
    }
    
    public void evict(String key) {
        redisTemplate.delete(key);
        log.info(" Evicted key from Redis cache: {}", key);
    }
    
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
