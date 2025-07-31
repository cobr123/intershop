package com.example.intershop.controller;

import com.example.intershop.api.DefaultApi;
import com.example.intershop.client.ApiClient;
import com.example.intershop.configuration.SecurityConfig;
import com.example.intershop.domain.BalanceUserIdGet200Response;
import com.example.intershop.model.*;
import com.example.intershop.service.OAuth2Service;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import com.example.intershop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

@WebFluxTest(CartItemsController.class)
@Import(SecurityConfig.class)
public class CartItemsControllerTest {

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
    }

    @Test
    public void testCartItemsListNoAuth() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        var get200Response = new BalanceUserIdGet200Response();
        get200Response.setBalance(BigDecimal.ONE);

        doReturn(Mono.just(order)).when(orderService).findNewOrder(anyLong());
        doReturn(Flux.just(item)).when(orderItemService).findByOrderId(anyLong());
        doReturn(Mono.just(ResponseEntity.ok(get200Response))).when(defaultApi).balanceUserIdGetWithHttpInfo(1L);

        webTestClient.get().uri("/cart/items").exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/login");
    }

    @Test
    public void testCartItemsList() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        var get200Response = new BalanceUserIdGet200Response();
        get200Response.setBalance(BigDecimal.ONE);

        var user = new UserUi(2L, "userName", "");

        doReturn(Mono.just(user)).when(userService).findByName(anyString());
        doReturn(Mono.just(order)).when(orderService).findNewOrder(anyLong());
        doReturn(Flux.just(item)).when(orderItemService).findByOrderId(anyLong());
        doReturn(Mono.just(ResponseEntity.ok(get200Response))).when(defaultApi).balanceUserIdGetWithHttpInfo(1L);

        doReturn(Mono.just("test_token")).when(oAuth2Service).getTokenValue();
        doReturn(apiClient).when(defaultApi).getApiClient();
        doReturn(apiClient).when(apiClient).addDefaultHeader(anyString(), anyString());
        doReturn(Mono.just(ResponseEntity.ok().build())).when(defaultApi).balanceUserIdGetWithHttpInfo(anyLong());

        webTestClient
                .mutateWith(mockUser(user.getName()))
                .mutateWith(csrf())
                .get().uri("/cart/items").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody()
                .xpath("//table/tr/td/table/tr/td/img").exists();
    }

    @Test
    public void testCartItemsListAddNoAuth() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        doReturn(Mono.just(order)).when(orderService).findNewOrder(anyLong());
        doReturn(Mono.just(item)).when(orderItemService).update(anyLong(), anyLong(), any());

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/cart/items/1")
                        .queryParam("action", "Plus").build()
                ).exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/login");

        verify(orderItemService, never()).update(anyLong(), anyLong(), any());
    }

    @Test
    public void testCartItemsListAdd() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        var user = new UserUi(2L, "userName", "");

        doReturn(Mono.just(user)).when(userService).findByName(anyString());
        doReturn(Mono.just(order)).when(orderService).findNewOrder(anyLong());
        doReturn(Mono.just(item)).when(orderItemService).update(anyLong(), anyLong(), any());

        doReturn(Mono.just("test_token")).when(oAuth2Service).getTokenValue();
        doReturn(apiClient).when(defaultApi).getApiClient();
        doReturn(apiClient).when(apiClient).addDefaultHeader(anyString(), anyString());
        doReturn(Mono.just(ResponseEntity.ok().build())).when(defaultApi).balanceUserIdGetWithHttpInfo(anyLong());

        webTestClient
                .mutateWith(mockUser(user.getName()))
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder.path("/cart/items/1")
                        .queryParam("action", "Plus").build()
                ).exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");

        verify(orderItemService).update(anyLong(), anyLong(), any());
    }

}