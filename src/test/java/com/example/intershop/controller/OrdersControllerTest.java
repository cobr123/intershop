package com.example.intershop.controller;

import com.example.intershop.model.*;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@WebFluxTest(OrdersController.class)
public class OrdersControllerTest {

    @MockitoBean
    private OrderService orderService;
    @MockitoBean
    private OrderItemService orderItemService;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        Mockito.reset(orderService);
        Mockito.reset(orderItemService);
    }

    @Test
    public void testOrdersList() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        doReturn(Flux.just(order)).when(orderService).findAllNotNew();
        doReturn(Flux.just(item)).when(orderItemService).findByOrderId(anyLong());

        webTestClient.get().uri("/orders").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody()
                .xpath("//table/tr/td/h2/a").exists();
    }

    @Test
    public void testOrderCard() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        doReturn(Mono.just(order)).when(orderService).findById(anyLong());
        doReturn(Flux.just(item)).when(orderItemService).findByOrderId(anyLong());

        webTestClient.get().uri("/orders/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody()
                .xpath("//table/tr/td/h2").exists();
    }

    @Test
    public void testNewOrderCard() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        doReturn(Mono.just(order)).when(orderService).findById(anyLong());
        doReturn(Flux.just(item)).when(orderItemService).findByOrderId(anyLong());

        webTestClient.get().uri("/orders/1?newOrder=true").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody()
                .xpath("//table/tr/td/h2").exists();
    }

}