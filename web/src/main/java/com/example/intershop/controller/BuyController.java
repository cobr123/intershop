package com.example.intershop.controller;

import com.example.intershop.api.DefaultApi;
import com.example.intershop.domain.BalancePostRequest;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/buy")
public class BuyController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    private final DefaultApi api;

    public BuyController(
            OrderService orderService,
            OrderItemService orderItemService,
            DefaultApi api
    ) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.api = api;
    }

    @PostMapping
    public Mono<String> update() {
        return orderService.findNewOrder()
                .flatMap(order -> orderItemService.getTotalSumByOrderId(order.getId()).zipWith(Mono.just(order)))
                .flatMap(pair -> {
                    var sum = pair.getT1();
                    var order = pair.getT2();
                    var postBody = new BalancePostRequest();
                    postBody.setSum(sum);

                    return api.balancePostWithHttpInfo(postBody)
                            .flatMap(resp -> {
                                if (resp.getStatusCode().is2xxSuccessful()) {
                                    return orderService.changeNewStatusToGathering(order)
                                            .thenReturn("redirect:/orders/" + order.getId() + "?newOrder=true");
                                } else {
                                    return Mono.just("redirect:/orders/" + order.getId());
                                }
                            });
                });
    }

}