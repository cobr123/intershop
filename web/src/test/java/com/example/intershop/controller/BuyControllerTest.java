package com.example.intershop.controller;

import com.example.intershop.api.DefaultApi;
import com.example.intershop.configuration.SecurityConfig;
import com.example.intershop.model.Order;
import com.example.intershop.model.OrderStatus;
import com.example.intershop.model.User;
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
    private DefaultApi api;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        Mockito.reset(orderService);
        Mockito.reset(orderItemService);
        Mockito.reset(userService);
        Mockito.reset(api);
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

        var user = new User(2L, "userName", "");

        doReturn(Mono.just(user)).when(userService).findByName(anyString());
        doReturn(Mono.just(order)).when(orderService).findNewOrder(anyLong());
        doReturn(Mono.just(BigDecimal.ONE)).when(orderItemService).getTotalSumByOrderId(anyLong());
        doReturn(Mono.just(ResponseEntity.ok().build())).when(api).balancePostWithHttpInfo(any());
        doReturn(Mono.just(order)).when(orderService).changeNewStatusToGathering(any());

        webTestClient
                .mutateWith(mockUser(user.getUsername()))
                .mutateWith(csrf())
                .post().uri("/buy").exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/orders/1?newOrder=true");
    }

}