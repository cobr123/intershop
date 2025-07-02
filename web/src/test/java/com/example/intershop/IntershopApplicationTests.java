package com.example.intershop;

import com.example.intershop.config.PostgreSqlTestContainer;
import com.example.intershop.config.RedisTestContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest
@Testcontainers
@ImportTestcontainers({PostgreSqlTestContainer.class, RedisTestContainer.class})
@ActiveProfiles("test")
public class IntershopApplicationTests {

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", PostgreSqlTestContainer::r2dbcUrl);
        registry.add("spring.r2dbc.username", PostgreSqlTestContainer.postgresqlContainer::getUsername);
        registry.add("spring.r2dbc.password", PostgreSqlTestContainer.postgresqlContainer::getPassword);
    }

}
