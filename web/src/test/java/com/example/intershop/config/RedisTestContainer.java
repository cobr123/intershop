package com.example.intershop.config;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public final class RedisTestContainer {

    @Container
    @ServiceConnection
    public static final RedisContainer redisContainer =
            new RedisContainer(DockerImageName.parse("redis:8-alpine"));
}
