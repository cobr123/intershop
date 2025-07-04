package com.example.intershop.controller;

import com.example.intershop.api.DefaultApi;
import com.example.intershop.model.Order;
import com.example.intershop.model.OrderStatus;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

@WebFluxTest(BuyController.class)
public class BuyControllerTest {

    @MockitoBean
    private OrderService orderService;
    @MockitoBean
    private OrderItemService orderItemService;

    @MockitoBean
    private DefaultApi api;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        Mockito.reset(orderService);
        Mockito.reset(orderItemService);
        Mockito.reset(api);
    }

    @Test
    public void testBuy() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        doReturn(Mono.just(order)).when(orderService).findNewOrder();
        doReturn(Mono.just(BigDecimal.ONE)).when(orderItemService).getTotalSumByOrderId(anyLong());
        doReturn(Mono.just(ResponseEntity.ok().build())).when(api).balancePostWithHttpInfo(any());

        webTestClient.post().uri("/buy").exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/orders/1?newOrder=true");
    }

}