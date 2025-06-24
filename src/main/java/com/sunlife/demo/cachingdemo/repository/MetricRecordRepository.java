package com.sunlife.demo.cachingdemo.repository;

import com.sunlife.demo.cachingdemo.model.MetricRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetricRecordRepository extends JpaRepository<MetricRecord, Long> {
    
    @Query("SELECT m FROM MetricRecord m ORDER BY m.createdAt DESC")
    List<MetricRecord> findAllOrderByCreatedAtDesc();
    
    @Query("SELECT m FROM MetricRecord m WHERE m.cacheType = :cacheType ORDER BY m.createdAt DESC")
    List<MetricRecord> findByCacheTypeOrderByCreatedAtDesc(MetricRecord.CacheType cacheType);
    
    @Query("SELECT m FROM MetricRecord m ORDER BY m.createdAt DESC LIMIT :limit")
    List<MetricRecord> findTopNOrderByCreatedAtDesc(int limit);
}
