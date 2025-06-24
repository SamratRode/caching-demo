package com.sunlife.demo.cachingdemo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CacheDataRequest {
    
    @NotBlank(message = "Key cannot be blank")
    @JsonProperty("key")
    private String key;
    
    @NotNull(message = "Value cannot be null")
    @JsonProperty("value")
    private Map<String, Object> value;
}
