package com.sunlife.demo.cachingdemo.controller;

import com.sunlife.demo.cachingdemo.dto.CacheDataResponse;
import com.sunlife.demo.cachingdemo.service.cache.RedisCacheService;
import com.sunlife.demo.cachingdemo.service.cache.MemcachedCacheService;
import com.sunlife.demo.cachingdemo.service.cache.HazelcastCacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cache")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cache Operations", description = "Endpoints for cache-specific data retrieval")
public class CacheController {
    
    private final RedisCacheService redisCacheService;
    private final MemcachedCacheService memcachedCacheService;
    private final HazelcastCacheService hazelcastCacheService;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @GetMapping("/redis/{key}")
    @Operation(
        summary = "Get data using Redis cache", 
        description = "Retrieve data using Redis as the caching layer"
    )
    @ApiResponse(responseCode = "200", description = "Data retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Data not found")
    public ResponseEntity<CacheDataResponse> getFromRedis(@PathVariable String key) {
        log.info(" Redis cache request for key: {}", key);
        
        long startTime = System.currentTimeMillis();
        Map<String, Object> value = redisCacheService.getValue(key);
        long duration = System.currentTimeMillis() - startTime;
        
        if (value != null) {
            CacheDataResponse response = new CacheDataResponse(
                key,
                value,
                "REDIS_CACHE",
                duration,
                LocalDateTime.now().plusMinutes(30).format(FORMATTER)
            );
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/memcached/{key}")
    @Operation(
        summary = "Get data using Memcached cache", 
        description = "Retrieve data using Memcached as the caching layer"
    )
    @ApiResponse(responseCode = "200", description = "Data retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Data not found")
    public ResponseEntity<CacheDataResponse> getFromMemcached(@PathVariable String key) {
        log.info(" Memcached cache request for key: {}", key);
        
        long startTime = System.currentTimeMillis();
        Map<String, Object> value = memcachedCacheService.getValue(key);
        long duration = System.currentTimeMillis() - startTime;
        
        if (value != null) {
            CacheDataResponse response = new CacheDataResponse(
                key,
                value,
                "MEMCACHED_CACHE",
                duration,
                LocalDateTime.now().plusMinutes(30).format(FORMATTER)
            );
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/hazelcast/{key}")
    @Operation(
        summary = "Get data using Hazelcast cache", 
        description = "Retrieve data using Hazelcast as the caching layer"
    )
    @ApiResponse(responseCode = "200", description = "Data retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Data not found")
    public ResponseEntity<CacheDataResponse> getFromHazelcast(@PathVariable String key) {
        log.info(" Hazelcast cache request for key: {}", key);
        
        long startTime = System.currentTimeMillis();
        Map<String, Object> value = hazelcastCacheService.getValue(key);
        long duration = System.currentTimeMillis() - startTime;
        
        if (value != null) {
            CacheDataResponse response = new CacheDataResponse(
                key,
                value,
                "HAZELCAST_CACHE",
                duration,
                LocalDateTime.now().plusMinutes(30).format(FORMATTER)
            );
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/redis/{key}")
    @Operation(summary = "Evict key from Redis cache")
    public ResponseEntity<Void> evictFromRedis(@PathVariable String key) {
        redisCacheService.evict(key);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/memcached/{key}")
    @Operation(summary = "Evict key from Memcached cache")
    public ResponseEntity<Void> evictFromMemcached(@PathVariable String key) {
        memcachedCacheService.evict(key);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/hazelcast/{key}")
    @Operation(summary = "Evict key from Hazelcast cache")
    public ResponseEntity<Void> evictFromHazelcast(@PathVariable String key) {
        hazelcastCacheService.evict(key);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/hazelcast/stats")
    @Operation(summary = "Get Hazelcast cache statistics")
    public ResponseEntity<Map<String, Object>> getHazelcastStats() {
        return ResponseEntity.ok(hazelcastCacheService.getCacheStats());
    }
}
