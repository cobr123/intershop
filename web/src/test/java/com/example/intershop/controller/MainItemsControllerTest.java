package com.example.intershop.controller;

import com.example.intershop.configuration.SecurityConfig;
import com.example.intershop.model.*;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import com.example.intershop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

@WebFluxTest(MainItemsController.class)
@Import(SecurityConfig.class)
public class MainItemsControllerTest {

    @MockitoBean
    private OrderService orderService;
    @MockitoBean
    private OrderItemService orderItemService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        Mockito.reset(orderService);
        Mockito.reset(orderItemService);
        Mockito.reset(userService);
    }

    @Test
    public void testMainItemsListNoAuth() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item1 = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));
        var item2 = new ItemUi(2L, "title2", "description2", "imgPath2", 2, BigDecimal.valueOf(2.2));
        var paging = new Paging(1, 10, false, false);
        Items items = new Items(ItemUi.grouped(List.of(item1, item2)), paging);

        doReturn(Mono.just(order)).when(orderService).findNewOrder(anyLong());
        doReturn(Mono.just(items)).when(orderItemService).findAll(anyLong(), any(), anyInt(), anyInt());

        webTestClient.get().uri("/main/items").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody()
                .xpath("//table/tr/td/table/tr/td/a").exists();
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

        var user = new User(2L, "userName", "");

        doReturn(Mono.just(user)).when(userService).findByName(anyString());
        doReturn(Mono.just(order)).when(orderService).findNewOrder(anyLong());
        doReturn(Mono.just(items)).when(orderItemService).findAll(anyLong(), any(), anyInt(), anyInt());

        webTestClient
                .mutateWith(mockUser(user.getUsername()))
                .mutateWith(csrf())
                .get().uri("/main/items").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody()
                .xpath("//table/tr/td/table/tr/td/a").exists();
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

        var user = new User(2L, "userName", "");

        doReturn(Mono.just(user)).when(userService).findByName(anyString());
        doReturn(Mono.just(order)).when(orderService).findNewOrder(anyLong());
        doReturn(Mono.just(items)).when(orderItemService).findByTitleLikeOrDescriptionLike(anyLong(), any(), any(), anyInt(), anyInt());

        webTestClient
                .mutateWith(mockUser(user.getUsername()))
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path("/main/items")
                        .queryParam("search", "tl")
                        .queryParam("sort", "Price").build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody()
                .xpath("//table/tr/td/table/tr/td/a").exists();
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

        var user = new User(2L, "userName", "");

        doReturn(Mono.just(user)).when(userService).findByName(anyString());
        doReturn(Mono.just(order)).when(orderService).findNewOrder(anyLong());
        doReturn(Mono.just(items)).when(orderItemService).findAll(anyLong(), any(), anyInt(), anyInt());

        webTestClient
                .mutateWith(mockUser(user.getUsername()))
                .mutateWith(csrf())
                .get().uri(uriBuilder -> uriBuilder.path("/main/items")
                        .queryParam("pageSize", "10")
                        .queryParam("pageNumber", "1").build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody()
                .xpath("//table/tr/td/table/tr/td/a").exists();
    }

    @Test
    public void testMainItemsListAddNoAuth() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        doReturn(Mono.just(order)).when(orderService).findNewOrder(anyLong());
        doReturn(Mono.just(item)).when(orderItemService).update(anyLong(), anyLong(), any());

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/main/items/1")
                        .queryParam("action", "PLUS").build()
                ).exchange()
                .expectStatus().isForbidden();

        verify(orderItemService, never()).update(any(), any(), any());
    }

    @Test
    public void testMainItemsListAdd() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        var user = new User(2L, "userName", "");

        doReturn(Mono.just(user)).when(userService).findByName(anyString());
        doReturn(Mono.just(order)).when(orderService).findNewOrder(anyLong());
        doReturn(Mono.just(item)).when(orderItemService).update(anyLong(), anyLong(), any());

        webTestClient
                .mutateWith(mockUser(user.getUsername()))
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder.path("/main/items/1")
                        .queryParam("action", "PLUS").build()
                ).exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main/items");

        verify(orderItemService).update(any(), any(), any());
    }

}