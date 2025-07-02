package com.example.intershop;

import com.example.intershop.model.Item;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@SpringBootApplication
@EnableCaching
public class IntershopWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntershopWebApplication.class, args);
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer cacheCustomizer() {
        return builder -> builder.withCacheConfiguration(
                "items",
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.of(1, ChronoUnit.MINUTES))
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new Jackson2JsonRedisSerializer<>(Item.class)
                                )
                        )
        );
    }
}
