package com.example.intershop.controller;

import com.example.intershop.model.Order;
import com.example.intershop.model.OrderUi;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrdersController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    public OrdersController(OrderService orderService, OrderItemService orderItemService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public String getAll(Model model) {
        List<OrderUi> orders = new ArrayList<>();
        for (Order o : orderService.findAll()) {
            var items = orderItemService.findByOrderId(o.getId());
            orders.add(new OrderUi(o.getId(), items));
        }
        model.addAttribute("orders", orders);

        return "orders";
    }

    @GetMapping("/{id}")
    public String getOrder(Model model, @PathVariable("id") Long orderId, @RequestParam(value = "newOrder", required = false, defaultValue = "false") Boolean newOrder) {
        Order order = orderService.findById(orderId).orElseThrow();
        var items = orderItemService.findByOrderId(order.getId());
        model.addAttribute("order", new OrderUi(order.getId(), items));
        model.addAttribute("newOrder", newOrder);

        return "orders";
    }

}