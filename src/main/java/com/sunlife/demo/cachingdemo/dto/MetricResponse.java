package com.sunlife.demo.cachingdemo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricResponse {
    
    @JsonProperty("cache_key")
    private String cacheKey;
    
    @JsonProperty("cache_type")
    private String cacheType;
    
    @JsonProperty("source")
    private String source;
    
    @JsonProperty("duration_ms")
    private Long durationMs;
    
    @JsonProperty("ttl_expires_at")
    private LocalDateTime ttlExpiresAt;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
