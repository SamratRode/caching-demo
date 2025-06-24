package com.sunlife.demo.cachingdemo.service.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunlife.demo.cachingdemo.model.CachedData;
import com.sunlife.demo.cachingdemo.model.MetricRecord;
import com.sunlife.demo.cachingdemo.service.DataService;
import com.sunlife.demo.cachingdemo.util.MetricCollector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemcachedCacheService {

    private final MemcachedClient memcachedClient;
    private final DataService dataService;
    private final MetricCollector metricCollector;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${cache.ttl-minutes}")
    private int ttlMinutes;

    @SuppressWarnings("unchecked")
    public Map<String, Object> getValue(String key) {
        long startTime = System.currentTimeMillis();

        try {
            Object cachedValue = memcachedClient.get(key);

            if (cachedValue != null) {
                long duration = System.currentTimeMillis() - startTime;
                LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(ttlMinutes);

                metricCollector.recordMetric(key, MetricRecord.CacheType.MEMCACHED,
                        MetricRecord.CacheSource.CACHE_HIT, duration, expiresAt);

                if (cachedValue instanceof String) {
                    return objectMapper.readValue((String) cachedValue, Map.class);
                } else {
                    return (Map<String, Object>) cachedValue;
                }
            }

            // Cache miss
            metricCollector.recordMetric(key, MetricRecord.CacheType.MEMCACHED,
                    MetricRecord.CacheSource.CACHE_MISS, 0L, null);

            // Fetch from DB
            long dbStartTime = System.currentTimeMillis();
            Optional<CachedData> dbResult = dataService.findByKey(key);

            if (dbResult.isPresent()) {
                Map<String, Object> value = dbResult.get().getCacheValue();
                long dbDuration = System.currentTimeMillis() - dbStartTime;
                LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(ttlMinutes);

                String jsonValue = objectMapper.writeValueAsString(value);
                memcachedClient.set(key, ttlMinutes * 60, jsonValue);

                metricCollector.recordMetric(key, MetricRecord.CacheType.MEMCACHED,
                        MetricRecord.CacheSource.DB_FETCH, dbDuration, expiresAt);

                return value;
            }

            return null;

        } catch (Exception e) {
            log.error(" Error accessing Memcached for key: {}", key, e);

            // Fallback to DB
            long dbStartTime = System.currentTimeMillis();
            Optional<CachedData> dbResult = dataService.findByKey(key);

            if (dbResult.isPresent()) {
                long dbDuration = System.currentTimeMillis() - dbStartTime;
                metricCollector.recordMetric(key, MetricRecord.CacheType.MEMCACHED,
                        MetricRecord.CacheSource.DB_FETCH, dbDuration, null);

                return dbResult.get().getCacheValue();
            }

            return null;
        }
    }

    public void setValue(String key, Map<String, Object> value) {
        try {
            dataService.saveData(key, value);

            String jsonValue = objectMapper.writeValueAsString(value);
            memcachedClient.set(key, ttlMinutes * 60, jsonValue);

            log.info("💾 Cached data in Memcached for key: {} with TTL: {} minutes", key, ttlMinutes);

        } catch (Exception e) {
            log.error(" Error caching data in Memcached for key: {}", key, e);
        }
    }

    public void evict(String key) {
        try {
            memcachedClient.delete(key);
            log.info(" Evicted key from Memcached cache: {}", key);
        } catch (Exception e) {
            log.error(" Error evicting key from Memcached: {}", key, e);
        }
    }

    public boolean exists(String key) {
        try {
            return memcachedClient.get(key) != null;
        } catch (Exception e) {
            log.error(" Error checking key existence in Memcached: {}", key, e);
            return false;
        }
    }
}
