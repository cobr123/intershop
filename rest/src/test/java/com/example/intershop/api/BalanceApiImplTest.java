package com.example.intershop.api;

import com.example.intershop.domain.BalanceGet200Response;
import com.example.intershop.domain.BalancePostRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

@WebFluxTest(BalanceApiImpl.class)
public class BalanceApiImplTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
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
}
