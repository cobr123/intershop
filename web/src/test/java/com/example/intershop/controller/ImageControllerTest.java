package com.example.intershop.controller;

import com.example.intershop.configuration.SecurityConfig;
import com.example.intershop.model.Item;
import com.example.intershop.service.ItemService;
import com.example.intershop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

@WebFluxTest(ImageController.class)
@Import(SecurityConfig.class)
public class ImageControllerTest {

    @MockitoBean
    private ItemService itemService;

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
        Mockito.reset(userService);
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