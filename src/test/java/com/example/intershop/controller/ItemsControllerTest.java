package com.example.intershop.controller;

import com.example.intershop.model.*;
import com.example.intershop.service.ItemService;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.io.File;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebFluxTest(ItemsController.class)
public class ItemsControllerTest {

    @MockitoBean
    private ItemService itemService;
    @MockitoBean
    private OrderService orderService;
    @MockitoBean
    private OrderItemService orderItemService;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        Mockito.reset(itemService);
        Mockito.reset(orderService);
        Mockito.reset(orderItemService);
    }

    @Test
    public void testGetItem() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        doReturn(Mono.just(order)).when(orderService).findNewOrder();
        doReturn(Mono.just(item)).when(orderItemService).findByOrderIdAndItemId(anyLong(), anyLong());

        var result = webTestClient.get().uri("/items/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody()
                .returnResult();

        MockMvcWebTestClient.resultActionsFor(result)
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"))
                .andExpect(xpath("//div/p/img").exists());
    }

    @Test
    public void testAddItemToCart() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        doReturn(Mono.just(order)).when(orderService).findNewOrder();
        doReturn(Mono.just(item)).when(orderItemService).update(anyLong(), anyLong(), any());

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/items/1")
                        .queryParam("action", "PLUS").build()
                ).exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/items/1");

        verify(orderItemService).update(any(), any(), any());
    }

    @Test
    public void testInsertItem() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new Item(1L, "1", "1", "", BigDecimal.valueOf(1));

        doReturn(Mono.just(item)).when(itemService).insert(any());

        File fileToUpload = new File("src/test/resources/application-test.properties");
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("image", new FileSystemResource(fileToUpload)).filename(fileToUpload.getName());

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/items")
                        .queryParam("title", "title")
                        .queryParam("description", "description")
                        .queryParam("price", "1")
                        .build()
                )
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/items/1");

        verify(itemService).insert(any());
    }
}