package com.example.intershop.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(RootController.class)
public class RootControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testRoot() {
        webTestClient.get().uri("/").exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main/items");
    }

}