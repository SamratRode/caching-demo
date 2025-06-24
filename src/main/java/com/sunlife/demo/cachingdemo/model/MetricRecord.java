package com.sunlife.demo.cachingdemo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "metric_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "cache_key", nullable = false)
    private String cacheKey;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "cache_type", nullable = false)
    private CacheType cacheType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private CacheSource source;
    
    @Column(name = "duration_ms", nullable = false)
    private Long durationMs;
    
    @Column(name = "ttl_expires_at")
    private LocalDateTime ttlExpiresAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum CacheType {
        REDIS, MEMCACHED, HAZELCAST
    }
    
    public enum CacheSource {
        CACHE_HIT, CACHE_MISS, DB_FETCH
    }
}
