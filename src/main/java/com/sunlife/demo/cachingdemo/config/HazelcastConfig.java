package com.sunlife.demo.cachingdemo.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.sunlife.demo.cachingdemo.service.cache.HazelcastFileMapStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class HazelcastConfig {
    
    @Value("${hazelcast.persistence.enabled}")
    private boolean persistenceEnabled;
    
    @Value("${hazelcast.persistence.base-dir}")
    private String persistenceBaseDir;
    
    @Value("${cache.ttl-minutes}")
    private int ttlMinutes;
    
    @Bean
    public Config hazelcastMainConfig() {
        Config config = new Config();
        config.setInstanceName("caching-demo-hazelcast");
        config.setClusterName("caching-demo-cluster");
        
        // Configure the cache map
        MapConfig mapConfig = new MapConfig();
        mapConfig.setName("cache-data");
        mapConfig.setTimeToLiveSeconds(ttlMinutes * 60);
        mapConfig.setMaxIdleSeconds(ttlMinutes * 60);
        
        // Configure persistence if enabled
        if (persistenceEnabled) {
            MapStoreConfig mapStoreConfig = new MapStoreConfig();
            mapStoreConfig.setEnabled(true);
            mapStoreConfig.setClassName(HazelcastFileMapStore.class.getName());
            mapStoreConfig.setWriteDelaySeconds(1);
            mapStoreConfig.setWriteBatchSize(100);
            mapStoreConfig.getProperties().put("base.dir", persistenceBaseDir);
            
            mapConfig.setMapStoreConfig(mapStoreConfig);
            log.info("✅ Hazelcast persistence enabled with base directory: {}", persistenceBaseDir);
        } else {
            log.info("ℹ️ Hazelcast persistence disabled");
        }
        
        config.addMapConfig(mapConfig);
        
        // Network configuration
        config.getNetworkConfig().setPort(5701);
        config.getNetworkConfig().setPortAutoIncrement(true);
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(false);
        
        return config;
    }
    
    @Bean
    public HazelcastInstance hazelcastInstance(Config hazelcastConfig) {
        HazelcastInstance instance = Hazelcast.newHazelcastInstance(hazelcastConfig);
        log.info("✅ Hazelcast instance created successfully");
        return instance;
    }
}
