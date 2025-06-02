package org.example.transactionaccept.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("transactionHistory");
        cacheManager.setCaffeine(Caffeine.newBuilder().maximumSize(1000).expireAfterWrite(30, TimeUnit.SECONDS));
        return cacheManager;
    }
}