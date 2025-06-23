package com.example.intershop.controller;

import com.example.intershop.model.Item;
import com.example.intershop.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

@WebFluxTest(ImageController.class)
public class ImageControllerTest {

    @MockitoBean
    private ItemService itemService;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        Mockito.reset(itemService);
    }

    @Test
    public void testGetImage() throws Exception {
        File tempFile = File.createTempFile("prefix-", "-suffix");
        tempFile.deleteOnExit();
        var item = new Item(1L, "title1", "description1", tempFile.getAbsolutePath(), BigDecimal.valueOf(1.1));

        doReturn(Mono.just(item)).when(itemService).findById(anyLong());

        webTestClient.get().uri("/images/1").exchange()
                .expectStatus().isOk();
    }

}