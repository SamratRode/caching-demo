package com.sunlife.demo.cachingdemo.controller;

import com.sunlife.demo.cachingdemo.dto.CacheDataRequest;
import com.sunlife.demo.cachingdemo.dto.CacheDataResponse;
import com.sunlife.demo.cachingdemo.model.CachedData;
import com.sunlife.demo.cachingdemo.service.DataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Data Management", description = "Endpoints for managing cached data")
public class DataController {
    
    private final DataService dataService;
    
    @PostMapping("/data")
    @Operation(
        summary = "Store data", 
        description = "Store key-value data in the database"
    )
    @ApiResponse(responseCode = "201", description = "Data stored successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    public ResponseEntity<CacheDataResponse> storeData(@Valid @RequestBody CacheDataRequest request) {
        log.info(" Received request to store data for key: {}", request.getKey());
        
        try {
            CachedData savedData = dataService.saveData(request.getKey(), request.getValue());
            
            CacheDataResponse response = new CacheDataResponse(
                savedData.getCacheKey(),
                savedData.getCacheValue(),
                "DATABASE",
                0L,
                null
            );
            
            log.info(" Successfully stored data for key: {}", request.getKey());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error(" Error storing data for key: {}", request.getKey(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/data/{key}")
    @Operation(
        summary = "Get data from database", 
        description = "Retrieve data directly from database (bypassing cache)"
    )
    @ApiResponse(responseCode = "200", description = "Data retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Data not found")
    public ResponseEntity<CacheDataResponse> getData(@PathVariable String key) {
        log.info(" Received request to get data for key: {}", key);
        
        try {
            return dataService.findByKey(key)
                .map(cachedData -> {
                    CacheDataResponse response = new CacheDataResponse(
                        cachedData.getCacheKey(),
                        cachedData.getCacheValue(),
                        "DATABASE",
                        1000L, // Approximate DB latency
                        null
                    );
                    
                    log.info(" Successfully retrieved data for key: {}", key);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    log.info(" No data found for key: {}", key);
                    return ResponseEntity.notFound().build();
                });
                
        } catch (Exception e) {
            log.error(" Error retrieving data for key: {}", key, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/data/{key}")
    @Operation(
        summary = "Delete data", 
        description = "Delete data from database"
    )
    @ApiResponse(responseCode = "204", description = "Data deleted successfully")
    @ApiResponse(responseCode = "404", description = "Data not found")
    public ResponseEntity<Void> deleteData(@PathVariable String key) {
        log.info("🗑️ Received request to delete data for key: {}", key);
        
        try {
            if (dataService.existsByKey(key)) {
                dataService.deleteByKey(key);
                log.info(" Successfully deleted data for key: {}", key);
                return ResponseEntity.noContent().build();
            } else {
                log.info(" No data found to delete for key: {}", key);
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            log.error(" Error deleting data for key: {}", key, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
