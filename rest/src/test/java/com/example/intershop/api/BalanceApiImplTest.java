package com.example.intershop.api;

import com.example.intershop.domain.BalanceGet200Response;
import com.example.intershop.domain.BalancePostRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

@WebFluxTest(BalanceApiImpl.class)
public class BalanceApiImplTest {

    @Autowired
    private WebTestClient webTestClient;

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @WithMockUser
    public void getBalanceTest() {
        var expectedBalance = new BalanceGet200Response();
        expectedBalance.setBalance(new BigDecimal(1000));

        webTestClient.get().uri("/balance").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(BalanceGet200Response.class)
                .isEqualTo(expectedBalance);
    }

    @Test
    public void getBalanceWoAuthTest() {
        webTestClient.get().uri("/balance").exchange()
                .expectStatus().isUnauthorized()
                .expectBody().isEmpty();
    }

    @Test
    @WithMockUser
    public void reduceBalanceTest() {
        var postBody = new BalancePostRequest();
        postBody.setSum(new BigDecimal(100));

        webTestClient.post()
                .uri("/balance")
                .bodyValue(postBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();

        var expectedBalance = new BalanceGet200Response();
        expectedBalance.setBalance(new BigDecimal(900));

        webTestClient.get().uri("/balance").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(BalanceGet200Response.class)
                .isEqualTo(expectedBalance);
    }

    @Test
    public void reduceBalanceWoAuthTest() {
        var postBody = new BalancePostRequest();
        postBody.setSum(new BigDecimal(100));

        webTestClient.post()
                .uri("/balance")
                .bodyValue(postBody)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody().isEmpty();

        webTestClient.get().uri("/balance").exchange()
                .expectStatus().isUnauthorized()
                .expectBody().isEmpty();;
    }

    @Test
    @WithMockUser
    public void overReduceBalanceTest() {
        var postBody = new BalancePostRequest();
        postBody.setSum(new BigDecimal(1100));

        webTestClient.post()
                .uri("/balance")
                .bodyValue(postBody)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody().isEmpty();
    }

    @Test
    public void overReduceBalanceWoAuthTest() {
        var postBody = new BalancePostRequest();
        postBody.setSum(new BigDecimal(1100));

        webTestClient.post()
                .uri("/balance")
                .bodyValue(postBody)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody().isEmpty();
    }
}
