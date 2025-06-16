package com.example.intershop.controller;

import com.example.intershop.model.Order;
import com.example.intershop.model.OrderStatus;
import com.example.intershop.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/buy")
public class BuyController {

    private final OrderService orderService;

    public BuyController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public String update() {
        Order order = orderService.findNewOrder();
        order.setStatus(OrderStatus.GATHERING);
        orderService.update(order);

        return "redirect:/orders/" + order.getId() + "?newOrder=true";
    }

}