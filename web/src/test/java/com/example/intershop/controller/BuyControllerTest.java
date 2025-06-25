package com.example.intershop.controller;

import com.example.intershop.model.Order;
import com.example.intershop.model.OrderStatus;
import com.example.intershop.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@WebFluxTest(BuyController.class)
public class BuyControllerTest {

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        Mockito.reset(orderService);
    }

    @Test
    public void testBuy() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        doReturn(Mono.just(order)).when(orderService).changeNewStatusToGathering();

        webTestClient.post().uri("/buy").exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/orders/1?newOrder=true");

        verify(orderService).changeNewStatusToGathering();
    }

}