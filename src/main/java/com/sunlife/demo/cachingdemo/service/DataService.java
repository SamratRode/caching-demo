package com.sunlife.demo.cachingdemo.service;

import com.sunlife.demo.cachingdemo.model.CachedData;
import com.sunlife.demo.cachingdemo.repository.CachedDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataService {
    
    private final CachedDataRepository cachedDataRepository;
    
    @Transactional
    public CachedData saveData(String key, Map<String, Object> value) {
        log.info(" Saving data to database for key: {}", key);
        
        Optional<CachedData> existing = cachedDataRepository.findByCacheKey(key);
        
        CachedData cachedData;
        if (existing.isPresent()) {
            cachedData = existing.get();
            cachedData.setCacheValue(value);
            log.info(" Updated existing data for key: {}", key);
        } else {
            cachedData = new CachedData();
            cachedData.setCacheKey(key);
            cachedData.setCacheValue(value);
            log.info(" Created new data entry for key: {}", key);
        }
        
        return cachedDataRepository.save(cachedData);
    }
    
    @Transactional(readOnly = true)
    public Optional<CachedData> findByKey(String key) {
        log.info(" Searching database for key: {}", key);
        
        // Simulate network latency of 1 second
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn(" Database query interrupted for key: {}", key);
        }
        
        Optional<CachedData> result = cachedDataRepository.findByCacheKey(key);
        
        if (result.isPresent()) {
            log.info(" Found data in database for key: {}", key);
        } else {
            log.info(" No data found in database for key: {}", key);
        }
        
        return result;
    }
    
    @Transactional(readOnly = true)
    public boolean existsByKey(String key) {
        return cachedDataRepository.existsByCacheKey(key);
    }
    
    @Transactional
    public void deleteByKey(String key) {
        log.info(" Deleting data from database for key: {}", key);
        cachedDataRepository.deleteByCacheKey(key);
    }
}
