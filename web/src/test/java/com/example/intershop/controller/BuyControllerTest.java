package com.example.intershop.controller;

import com.example.intershop.api.DefaultApi;
import com.example.intershop.client.ApiClient;
import com.example.intershop.configuration.SecurityConfig;
import com.example.intershop.model.Order;
import com.example.intershop.model.OrderStatus;
import com.example.intershop.model.UserUi;
import com.example.intershop.service.OAuth2Service;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import com.example.intershop.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

@WebFluxTest(BuyController.class)
@Import(SecurityConfig.class)
public class BuyControllerTest {

    @MockitoBean
    private OrderService orderService;
    @MockitoBean
    private OrderItemService orderItemService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private DefaultApi defaultApi;
    @MockitoBean
    private ApiClient apiClient;

    @MockitoBean
    private OAuth2Service oAuth2Service;
    @MockitoBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;
    @MockitoBean
    private ReactiveOAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        Mockito.reset(orderService);
        Mockito.reset(orderItemService);
        Mockito.reset(userService);
        Mockito.reset(defaultApi);
        Mockito.reset(apiClient);
        Mockito.reset(oAuth2Service);
    }

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testBuy() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var user = new UserUi(2L, "userName", "");

        doReturn(Mono.just(user)).when(userService).findByName(anyString());
        doReturn(Mono.just(order)).when(orderService).findNewOrder(anyLong());
        doReturn(Mono.just(BigDecimal.ONE)).when(orderItemService).getTotalSumByOrderId(anyLong());
        doReturn(Mono.just(order)).when(orderService).changeNewStatusToGathering(any());

        doReturn(Mono.just("test_token")).when(oAuth2Service).getTokenValue();
        doReturn(apiClient).when(defaultApi).getApiClient();
        doReturn(apiClient).when(apiClient).addDefaultHeader(anyString(), anyString());
        doReturn(Mono.just(ResponseEntity.ok().build())).when(defaultApi).balancePostWithHttpInfo(any());

        webTestClient
                .mutateWith(mockUser(user.getName()))
                .mutateWith(csrf())
                .post().uri("/buy").exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/orders/1?newOrder=true");
    }

}