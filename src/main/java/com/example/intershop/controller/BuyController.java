package com.example.intershop.controller;

import com.example.intershop.model.OrderStatus;
import com.example.intershop.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/buy")
public class BuyController {

    private final OrderService orderService;

    public BuyController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Mono<String> update() {
        return orderService.findNewOrder()
                .flatMap(order -> {
                    order.setStatus(OrderStatus.GATHERING);
                    return orderService.update(order);
                })
                .map(order -> "redirect:/orders/" + order.getId() + "?newOrder=true");
    }

}