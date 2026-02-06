package com.exposer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import tools.jackson.databind.json.JsonMapper;

import static com.exposer.constants.RedisConstants.GLOBAL_REDIS_TTL;


@Configuration
public class RedisConfig {

    @Bean
    public JsonMapper jsonMapper() {

        return JsonMapper.builder()
                .findAndAddModules()
                .build();
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration configuration = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(GLOBAL_REDIS_TTL);

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(configuration)
                .build();
    }
}
