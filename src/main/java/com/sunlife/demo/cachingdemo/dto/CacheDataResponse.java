package com.sunlife.demo.cachingdemo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CacheDataResponse {
    
    @JsonProperty("key")
    private String key;
    
    @JsonProperty("value")
    private Map<String, Object> value;
    
    @JsonProperty("source")
    private String source;
    
    @JsonProperty("duration_ms")
    private Long durationMs;
    
    @JsonProperty("ttl_expires_at")
    private String ttlExpiresAt;
}
