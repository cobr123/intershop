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
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebFluxTest(MainItemsController.class)
public class MainItemsControllerTest {

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
    public void testMainItemsList() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item1 = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));
        var item2 = new ItemUi(2L, "title2", "description2", "imgPath2", 2, BigDecimal.valueOf(2.2));
        var paging = new Paging(1, 10, false, false);
        Items items = new Items(ItemUi.grouped(List.of(item1, item2)), paging);

        doReturn(Mono.just(order)).when(orderService).findNewOrder();
        doReturn(Mono.just(items)).when(orderItemService).findAll(anyLong(), any(), anyInt(), anyInt());

        var result = webTestClient.get().uri("/main/items").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody()
                .returnResult();

        MockMvcWebTestClient.resultActionsFor(result)
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("sort"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(xpath("//table/tr/td/table/tr/td/a").exists());
    }

    @Test
    public void testSearchMainItemsList() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item1 = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));
        var item2 = new ItemUi(2L, "title2", "description2", "imgPath2", 2, BigDecimal.valueOf(2.2));
        var paging = new Paging(1, 10, false, false);
        Items items = new Items(ItemUi.grouped(List.of(item1, item2)), paging);

        doReturn(Mono.just(order)).when(orderService).findNewOrder();
        doReturn(Mono.just(items)).when(orderItemService).findByTitleLikeOrDescriptionLike(anyLong(), any(), any(), anyInt(), anyInt());

        var result = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/main/items")
                        .queryParam("search", "tl")
                        .queryParam("sort", "Price").build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody()
                .returnResult();

        MockMvcWebTestClient.resultActionsFor(result)
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("sort"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(xpath("//table/tr/td/table/tr/td/a").exists());
    }

    @Test
    public void testSearchPaginatedMainItemsList() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item1 = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));
        var item2 = new ItemUi(2L, "title2", "description2", "imgPath2", 2, BigDecimal.valueOf(2.2));
        var paging = new Paging(1, 10, false, false);
        Items items = new Items(ItemUi.grouped(List.of(item1, item2)), paging);

        doReturn(Mono.just(order)).when(orderService).findNewOrder();
        doReturn(Mono.just(items)).when(orderItemService).findAll(anyLong(), any(), anyInt(), anyInt());

        var result = webTestClient.get().uri(uriBuilder -> uriBuilder.path("/main/items")
                        .queryParam("pageSize", "10")
                        .queryParam("pageNumber", "1").build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody()
                .returnResult();

        MockMvcWebTestClient.resultActionsFor(result)
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("sort"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(xpath("//table/tr/td/table/tr/td/a").exists());
    }


    @Test
    public void testMainItemsListAdd() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        doReturn(Mono.just(order)).when(orderService).findNewOrder();
        doReturn(Mono.just(item)).when(orderItemService).update(anyLong(), anyLong(), any());

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/main/items/1")
                        .queryParam("action", "PLUS").build()
                ).exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main/items");

        verify(orderItemService).update(any(), any(), any());
    }

}