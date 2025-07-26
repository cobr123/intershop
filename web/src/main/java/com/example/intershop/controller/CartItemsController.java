package com.example.intershop.controller;

import com.example.intershop.api.DefaultApi;
import com.example.intershop.domain.BalanceGetRequest;
import com.example.intershop.model.*;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import com.example.intershop.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/cart/items")
public class CartItemsController {

    private static final Logger log = LoggerFactory.getLogger(CartItemsController.class);

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    private final UserService userService;

    private final DefaultApi api;
    private final ReactiveOAuth2AuthorizedClientManager manager;

    public CartItemsController(
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

    @GetMapping
    public Mono<String> getOrderItems(Model model) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findByName)
                .flatMap(user -> orderService.findNewOrder(user.getId()).zipWith(Mono.just(user)))
                .flatMap(pair -> orderItemService.findByOrderId(pair.getT1().getId())
                        .collectList()
                        .map(items -> Pair.of(pair, items))
                )
                .flatMap(pair -> manager.authorize(OAuth2AuthorizeRequest
                                        .withClientRegistrationId("keycloak-rest-client")
                                        .principal("system")
                                        .build()
                                )
                                .map(OAuth2AuthorizedClient::getAccessToken)
                                .map(OAuth2AccessToken::getTokenValue)
                                .flatMap(accessToken -> {
                                    var balanceGetRequest = new BalanceGetRequest();
                                    balanceGetRequest.setId(pair.getFirst().getT2().getId());
                                    api.getApiClient().addDefaultHeader("Authorization", "Bearer " + accessToken);
                                    return api.balanceGetWithHttpInfo(balanceGetRequest).timeout(Duration.of(5, ChronoUnit.SECONDS));
                                })
                                .map(resp -> {
                                    if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                                        return Pair.of(pair, resp.getBody().getBalance());
                                    } else {
                                        log.error("resp = {}", resp);
                                        return Pair.of(pair, BigDecimal.ONE.negate());
                                    }
                                })
                                .onErrorResume(err -> {
                                    log.error(err.getMessage(), err);
                                    return Mono.just(Pair.of(pair, BigDecimal.ONE.negate()));
                                })
                                .defaultIfEmpty(Pair.of(pair, BigDecimal.ONE.negate()))
                )
                .map(pair -> {
                    Order order = pair.getFirst().getFirst().getT1();
                    var items = pair.getFirst().getSecond();
                    var balance = pair.getSecond();
                    OrderUi orderUi = new OrderUi(order.getId(), items);
                    model.addAttribute("items", items);
                    model.addAttribute("total", orderUi.totalSum());
                    model.addAttribute("empty", items.isEmpty());
                    model.addAttribute("balance", balance);
                    return "cart";
                });
    }

    @PostMapping("/{id}")
    public Mono<String> update(@PathVariable("id") Long itemId, ChangeCountForm changeCountForm) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findByName)
                .flatMap(user -> orderService.findNewOrder(user.getId()))
                .flatMap(order -> orderItemService.update(order.getId(), itemId, changeCountForm.getAction()))
                .thenReturn("redirect:/cart/items");
    }

}