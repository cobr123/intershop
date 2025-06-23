package com.example.intershop.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public final class PostgreSqlTestContainer {

    @Container
    @ServiceConnection
    public static final PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:17-alpine");

    public static String r2dbcUrl() {
        return String.format("r2dbc:postgres://%s:%s/%s",
                postgresqlContainer.getHost(),
                postgresqlContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT),
                postgresqlContainer.getDatabaseName());
    }
}