package com.sunlife.demo.cachingdemo.repository;

import com.sunlife.demo.cachingdemo.model.CachedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CachedDataRepository extends JpaRepository<CachedData, Long> {
    
    Optional<CachedData> findByCacheKey(String cacheKey);
    
    boolean existsByCacheKey(String cacheKey);
    
    void deleteByCacheKey(String cacheKey);
}
