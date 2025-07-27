package com.example.intershop.api;

import com.example.intershop.configuration.SecurityConfig;
import com.example.intershop.domain.*;
import com.example.intershop.model.UserBalance;
import com.example.intershop.service.UserBalanceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@WebFluxTest(BalanceApiImpl.class)
@Import(SecurityConfig.class)
public class BalanceApiImplTest {

    @MockitoBean
    private UserBalanceService userBalanceService;

    @MockitoBean
    private ReactiveJwtDecoder reactiveJwtDecoder;

    @Autowired
    private WebTestClient webTestClient;

    @AfterEach
    public void clearSecurityContext() {
        Mockito.reset(userBalanceService);
        SecurityContextHolder.clearContext();
    }

    @Test
    @WithMockUser
    public void getBalanceTest() {
        var userBalance = new UserBalance(1L, new BigDecimal(1000));

        var expectedBalance = new BalanceGet200Response();
        expectedBalance.setBalance(userBalance.getBalance());

        doReturn(Mono.just(userBalance)).when(userBalanceService).findById(anyLong());

        var getBody = new BalanceGetRequest();
        getBody.setId(userBalance.getId());

        webTestClient
                .method(HttpMethod.GET)
                .uri("/balance")
                .bodyValue(getBody)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(BalanceGet200Response.class)
                .isEqualTo(expectedBalance);
    }

    @Test
    public void getBalanceWoAuthTest() {
        var userBalance = new UserBalance(1L, new BigDecimal(1000));
        var getBody = new BalanceGetRequest();
        getBody.setId(userBalance.getId());

        webTestClient
                .method(HttpMethod.GET)
                .uri("/balance")
                .bodyValue(getBody)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody().isEmpty();
    }

    @Test
    @WithMockUser
    public void reduceBalanceTest() {
        var userBalance = new UserBalance(1L, new BigDecimal(1000));

        var postBody = new BalancePostRequest();
        postBody.setId(userBalance.getId());
        postBody.setSum(new BigDecimal(100));

        doReturn(Mono.just(userBalance)).when(userBalanceService).findById(anyLong());
        doReturn(Mono.just(userBalance)).when(userBalanceService).save(any());

        webTestClient.post()
                .uri("/balance")
                .bodyValue(postBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();

        var getBody = new BalanceGetRequest();
        getBody.setId(userBalance.getId());

        var expectedBalance = new BalanceGet200Response();
        expectedBalance.setBalance(new BigDecimal(900));

        webTestClient
                .method(HttpMethod.GET)
                .uri("/balance")
                .bodyValue(getBody)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(BalanceGet200Response.class)
                .isEqualTo(expectedBalance);
    }

    @Test
    public void reduceBalanceWoAuthTest() {
        var userBalance = new UserBalance(1L, new BigDecimal(1000));
        var postBody = new BalancePostRequest();
        postBody.setId(userBalance.getId());
        postBody.setSum(new BigDecimal(100));

        webTestClient.post()
                .uri("/balance")
                .bodyValue(postBody)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody().isEmpty();

        webTestClient.get().uri("/balance").exchange()
                .expectStatus().isUnauthorized()
                .expectBody().isEmpty();
    }

    @Test
    @WithMockUser
    public void overReduceBalanceTest() {
        var userBalance = new UserBalance(1L, new BigDecimal(1000));

        var postBody = new BalancePostRequest();
        postBody.setId(userBalance.getId());
        postBody.setSum(new BigDecimal(1100));

        doReturn(Mono.just(userBalance)).when(userBalanceService).findById(anyLong());

        webTestClient.post()
                .uri("/balance")
                .bodyValue(postBody)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody().isEmpty();
    }

    @Test
    public void overReduceBalanceWoAuthTest() {
        var userBalance = new UserBalance(1L, new BigDecimal(1000));
        var postBody = new BalancePostRequest();
        postBody.setId(userBalance.getId());
        postBody.setSum(new BigDecimal(1100));

        webTestClient.post()
                .uri("/balance")
                .bodyValue(postBody)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody().isEmpty();
    }
}
