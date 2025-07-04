package com.example.intershop.controller;

import com.example.intershop.api.DefaultApi;
import com.example.intershop.domain.BalanceGet200Response;
import com.example.intershop.model.*;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@WebFluxTest(CartItemsController.class)
public class CartItemsControllerTest {

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
    public void testCartItemsList() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        var get200Response = new BalanceGet200Response();
        get200Response.setBalance(BigDecimal.ONE);

        doReturn(Mono.just(order)).when(orderService).findNewOrder();
        doReturn(Flux.just(item)).when(orderItemService).findByOrderId(anyLong());
        doReturn(Mono.just(ResponseEntity.ok(get200Response))).when(api).balanceGetWithHttpInfo();

        webTestClient.get().uri("/cart/items").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody()
                .xpath("//table/tr/td/table/tr/td/img").exists();
    }

    @Test
    public void testCartItemsListAdd() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        doReturn(Mono.just(order)).when(orderService).findNewOrder();
        doReturn(Mono.just(item)).when(orderItemService).update(anyLong(), anyLong(), any());

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/cart/items/1")
                        .queryParam("action", "Plus").build()
                ).exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");

        verify(orderItemService).update(anyLong(), anyLong(), any());
    }

}