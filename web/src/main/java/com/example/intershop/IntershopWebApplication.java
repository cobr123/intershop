package com.example.intershop;

import com.example.intershop.api.DefaultApi;
import com.example.intershop.client.ApiClient;
import com.example.intershop.model.Item;
import com.example.intershop.model.Items;
import com.example.intershop.model.Order;
import org.springframework.beans.factory.annotation.Value;
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
        return builder -> builder
                .withCacheConfiguration(
                        "items",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.of(1, ChronoUnit.MINUTES))
                                .serializeValuesWith(
                                        RedisSerializationContext.SerializationPair.fromSerializer(
                                                new Jackson2JsonRedisSerializer<>(Item.class)
                                        )
                                )
                )
                .withCacheConfiguration(
                        "orders",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.of(1, ChronoUnit.MINUTES))
                                .serializeValuesWith(
                                        RedisSerializationContext.SerializationPair.fromSerializer(
                                                new Jackson2JsonRedisSerializer<>(Order.class)
                                        )
                                )
                )
                .withCacheConfiguration(
                        "paged_order_items",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.of(1, ChronoUnit.MINUTES))
                                .serializeValuesWith(
                                        RedisSerializationContext.SerializationPair.fromSerializer(
                                                new Jackson2JsonRedisSerializer<>(Items.class)
                                        )
                                )
                );
    }

    @Bean
    public DefaultApi defaultApi(@Value("${REST_HOST:localhost}") String restHost, @Value("${REST_PORT:8081}") int restPort) {
        return new DefaultApi(new ApiClient().setBasePath("http://" + restHost + ":" + restPort));
    }
}
