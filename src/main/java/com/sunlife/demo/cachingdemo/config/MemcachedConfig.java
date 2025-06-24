package com.sunlife.demo.cachingdemo.config;

import lombok.extern.slf4j.Slf4j;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.AddrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class MemcachedConfig {

    @Value("${memcached.servers}")
    private String memcachedServers;

    @Bean
    public MemcachedClient memcachedClient() {
        try {
            MemcachedClient client = new MemcachedClient(AddrUtil.getAddresses(memcachedServers));
            log.info(" Memcached client initialized successfully with servers: {}", memcachedServers);
            return client;
        } catch (IOException e) {
            log.error(" Failed to initialize Memcached client", e);
            throw new RuntimeException("Failed to initialize Memcached client", e);
        }
    }
}
