package com.sunlife.demo.cachingdemo.service.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.map.MapStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
@Slf4j
public class HazelcastFileMapStore implements MapStore<String, Object> {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Path baseDir;
    

    public void init(Properties properties) {
        String baseDirPath = properties.getProperty("base.dir", "/tmp/hazelcast-data");
        this.baseDir = Paths.get(baseDirPath);
        
        try {
            Files.createDirectories(baseDir);
            log.info(" Hazelcast MapStore initialized with directory: {}", baseDir);
        } catch (IOException e) {
            log.error(" Failed to create Hazelcast persistence directory: {}", baseDir, e);
            throw new RuntimeException("Failed to initialize Hazelcast MapStore", e);
        }
    }
    
    @Override
    public Object load(String key) {
        Path filePath = baseDir.resolve(key + ".json");
        
        if (!Files.exists(filePath)) {
            log.debug("🔍 No persisted data found for key: {}", key);
            return null;
        }
        
        try {
            String content = Files.readString(filePath);
            Object value = objectMapper.readValue(content, Object.class);
            log.debug(" Loaded persisted data for key: {}", key);
            return value;
        } catch (IOException e) {
            log.error(" Failed to load persisted data for key: {}", key, e);
            return null;
        }
    }
    
    @Override
    public Map<String, Object> loadAll(Collection<String> keys) {
        Map<String, Object> result = new HashMap<>();
        
        for (String key : keys) {
            Object value = load(key);
            if (value != null) {
                result.put(key, value);
            }
        }
        
        log.debug(" Loaded {} persisted entries", result.size());
        return result;
    }
    
    @Override
    public Iterable<String> loadAllKeys() {
        try {
            return Files.walk(baseDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(path -> path.getFileName().toString())
                    .map(fileName -> fileName.substring(0, fileName.lastIndexOf('.')))
                    .toList();
        } catch (IOException e) {
            log.error(" Failed to load all keys from persistence", e);
            return java.util.Collections.emptyList();
        }
    }
    
    @Override
    public void store(String key, Object value) {
        Path filePath = baseDir.resolve(key + ".json");
        
        try {
            String content = objectMapper.writeValueAsString(value);
            Files.writeString(filePath, content);
            log.debug(" Persisted data for key: {}", key);
        } catch (JsonProcessingException e) {
            log.error(" Failed to serialize data for key: {}", key, e);
        } catch (IOException e) {
            log.error(" Failed to persist data for key: {}", key, e);
        }
    }
    
    @Override
    public void storeAll(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            store(entry.getKey(), entry.getValue());
        }
        log.debug(" Persisted {} entries to storage", map.size());
    }
    
    @Override
    public void delete(String key) {
        Path filePath = baseDir.resolve(key + ".json");
        
        try {
            Files.deleteIfExists(filePath);
            log.debug(" Deleted persisted data for key: {}", key);
        } catch (IOException e) {
            log.error(" Failed to delete persisted data for key: {}", key, e);
        }
    }
    
    @Override
    public void deleteAll(Collection<String> keys) {
        for (String key : keys) {
            delete(key);
        }
        log.debug(" Deleted {} persisted entries", keys.size());
    }
}
