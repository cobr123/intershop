package com.example.intershop.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public final class PostgreSqlTestContainer {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:17-alpine");

}