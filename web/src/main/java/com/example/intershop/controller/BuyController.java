package com.example.intershop.controller;

import com.example.intershop.api.DefaultApi;
import com.example.intershop.domain.BalancePostRequest;
import com.example.intershop.model.Order;
import com.example.intershop.model.UserUi;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import com.example.intershop.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/buy")
public class BuyController {

    private static final Logger log = LoggerFactory.getLogger(BuyController.class);

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    private final UserService userService;

    private final DefaultApi api;
    private final ReactiveOAuth2AuthorizedClientManager manager;

    public BuyController(
            OrderService orderService,
            OrderItemService orderItemService,
            UserService userService,
            DefaultApi api,
            ReactiveOAuth2AuthorizedClientManager manager
    ) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.userService = userService;
        this.api = api;
        this.manager = manager;
    }

    @PostMapping
    public Mono<String> update() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findByName)
                .flatMap(user -> orderService.findNewOrder(user.getId()).zipWith(Mono.just(user)))
                .flatMap(pair -> orderItemService.getTotalSumByOrderId(pair.getT1().getId()).zipWith(Mono.just(pair)))
                .flatMap(pair -> {
                    var sum = pair.getT1();
                    Order order = pair.getT2().getT1();
                    UserUi user = pair.getT2().getT2();
                    var postBody = new BalancePostRequest();
                    postBody.setId(user.getId());
                    postBody.setSum(sum);

                    return manager.authorize(OAuth2AuthorizeRequest
                                    .withClientRegistrationId("keycloak-rest-client")
                                    .principal("system")
                                    .build()
                            )
                            .map(OAuth2AuthorizedClient::getAccessToken)
                            .map(OAuth2AccessToken::getTokenValue)
                            .flatMap(accessToken -> {
                                api.getApiClient().addDefaultHeader("Authorization", "Bearer " + accessToken);
                                return api.balancePostWithHttpInfo(postBody);
                            })
                            .timeout(Duration.of(5, ChronoUnit.SECONDS))
                            .flatMap(resp -> {
                                if (resp.getStatusCode().is2xxSuccessful()) {
                                    return orderService.changeNewStatusToGathering(order)
                                            .thenReturn("redirect:/orders/" + order.getId() + "?newOrder=true");
                                } else {
                                    log.error("resp = {}", resp);
                                    return Mono.just("redirect:/cart/items");
                                }
                            })
                            .onErrorReturn("redirect:/cart/items");
                });
    }

}