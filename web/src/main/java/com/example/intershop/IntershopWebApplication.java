package com.example.intershop;

import com.example.intershop.api.DefaultApi;
import com.example.intershop.client.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IntershopWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntershopWebApplication.class, args);
    }

    @Bean
    public DefaultApi defaultApi(@Value("${REST_HOST:localhost}") String restHost, @Value("${REST_PORT:8081}") int restPort) {
        return new DefaultApi(new ApiClient().setBasePath("http://" + restHost + ":" + restPort));
    }
}
