package com.example.intershop.controller;

import com.example.intershop.api.DefaultApi;
import com.example.intershop.domain.BalancePostRequest;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import com.example.intershop.service.UserService;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
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

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    private final UserService userService;

    private final DefaultApi api;

    public BuyController(
            OrderService orderService,
            OrderItemService orderItemService,
            UserService userService,
            DefaultApi api
    ) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.userService = userService;
        this.api = api;
    }

    @PostMapping
    public Mono<String> update() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findByName)
                .flatMap(user -> orderService.findNewOrder(user.getId()))
                .flatMap(order -> orderItemService.getTotalSumByOrderId(order.getId()).zipWith(Mono.just(order)))
                .flatMap(pair -> {
                    var sum = pair.getT1();
                    var order = pair.getT2();
                    var postBody = new BalancePostRequest();
                    postBody.setSum(sum);

                    return api.balancePostWithHttpInfo(postBody)
                            .timeout(Duration.of(5, ChronoUnit.SECONDS))
                            .flatMap(resp -> {
                                if (resp.getStatusCode().is2xxSuccessful()) {
                                    return orderService.changeNewStatusToGathering(order)
                                            .thenReturn("redirect:/orders/" + order.getId() + "?newOrder=true");
                                } else {
                                    return Mono.just("redirect:/cart/items");
                                }
                            })
                            .onErrorReturn("redirect:/cart/items");
                });
    }

}