package com.example.intershop.controller;

import com.example.intershop.configuration.SecurityConfig;
import com.example.intershop.model.*;
import com.example.intershop.service.ItemService;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import com.example.intershop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

@WebFluxTest(ItemsController.class)
@Import(SecurityConfig.class)
public class ItemsControllerTest {

    @MockitoBean
    private ItemService itemService;
    @MockitoBean
    private OrderService orderService;
    @MockitoBean
    private OrderItemService orderItemService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;
    @MockitoBean
    private ReactiveOAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        Mockito.reset(itemService);
        Mockito.reset(orderService);
        Mockito.reset(orderItemService);
        Mockito.reset(userService);
    }

    @Test
    public void testGetItemNoAuth() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        doReturn(Mono.just(order)).when(orderService).findNewOrder(anyLong());
        doReturn(Mono.just(item)).when(orderItemService).findByOrderIdAndItemId(any(), anyLong());

        webTestClient.get().uri("/items/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody()
                .xpath("//div/p/img").exists()
                .xpath("//div/form").doesNotExist();
    }

    @Test
    public void testGetItem() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        var user = new UserUi(2L, "userName", "");

        doReturn(Mono.just(user)).when(userService).findByName(anyString());
        doReturn(Mono.just(order)).when(orderService).findNewOrder(anyLong());
        doReturn(Mono.just(item)).when(orderItemService).findByOrderIdAndItemId(anyLong(), anyLong());

        webTestClient
                .mutateWith(mockUser(user.getName()))
                .mutateWith(csrf())
                .get().uri("/items/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody()
                .xpath("//div/p/img").exists()
                .xpath("//div/form").exists();
    }

    @Test
    public void testAddItemToCartNoAuth() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        doReturn(Mono.just(order)).when(orderService).findNewOrder(anyLong());
        doReturn(Mono.just(item)).when(orderItemService).update(anyLong(), anyLong(), any());

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/items/1")
                        .queryParam("action", "PLUS").build()
                ).exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/login");

        verify(orderItemService, never()).update(any(), any(), any());
    }

    @Test
    public void testAddItemToCart() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        var user = new UserUi(2L, "userName", "");

        doReturn(Mono.just(user)).when(userService).findByName(anyString());
        doReturn(Mono.just(order)).when(orderService).findNewOrder(anyLong());
        doReturn(Mono.just(item)).when(orderItemService).update(anyLong(), anyLong(), any());

        webTestClient
                .mutateWith(mockUser(user.getName()))
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder.path("/items/1")
                        .queryParam("action", "PLUS").build()
                ).exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/items/1");

        verify(orderItemService).update(any(), any(), any());
    }

    @Test
    public void testInsertItemNoAuth() {
        var item = new Item(1L, "1", "1", "", BigDecimal.valueOf(1));

        doReturn(Mono.just(item)).when(itemService).insert(any());

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("title", "title");
        builder.part("description", "description");
        builder.part("price", BigDecimal.valueOf(1));

        webTestClient.post()
                .uri("/items")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/login");

        verify(itemService, never()).insert(any());
    }

    @Test
    public void testInsertItem() {
        var item = new Item(1L, "1", "1", "", BigDecimal.valueOf(1));

        var user = new UserUi(2L, "userName", "");

        doReturn(Mono.just(user)).when(userService).findByName(anyString());
        doReturn(Mono.just(item)).when(itemService).insert(any());

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("title", "title");
        builder.part("description", "description");
        builder.part("price", BigDecimal.valueOf(1));

        webTestClient
                .mutateWith(mockUser(user.getName()))
                .mutateWith(csrf())
                .post()
                .uri("/items")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/items/1");

        verify(itemService).insert(any());
    }

}