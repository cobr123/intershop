package com.example.intershop.controller;

import com.example.intershop.model.*;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart/items")
public class CartItemsController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    public CartItemsController(OrderService orderService, OrderItemService orderItemService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public String getOrderItems(Model model) {
        Order order = orderService.findNewOrder();
        List<Item> items = orderItemService.findByOrderId(order.getId());
        OrderUi orderUi = new OrderUi(order.getId(), items);
        model.addAttribute("items", items);
        model.addAttribute("total", orderUi.totalSum());
        model.addAttribute("empty", items.isEmpty());

        return "cart";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long itemId, @RequestParam("action") ItemAction action) {
        Order order = orderService.findNewOrder();
        orderItemService.update(order.getId(), itemId, action);

        return "redirect:/cart/items";
    }

}