package com.example.intershop.controller;

import com.example.intershop.model.*;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
    public Mono<String> getOrderItems(Model model) {
        return orderService.findNewOrder()
                .flatMap(order -> orderItemService.findByOrderId(order.getId())
                        .collectList()
                        .map(items -> Pair.of(order.getId(), items))
                )
                .map(pair -> {
                    var orderId = pair.getFirst();
                    var items = pair.getSecond();
                    OrderUi orderUi = new OrderUi(orderId, items);
                    model.addAttribute("items", items);
                    model.addAttribute("total", orderUi.totalSum());
                    model.addAttribute("empty", items.isEmpty());
                    return "cart";
                });
    }

    @PostMapping("/{id}")
    public Mono<String> update(@PathVariable("id") Long itemId, ChangeCountForm changeCountForm) {
        return orderService.findNewOrder()
                .flatMap(order -> orderItemService.update(order.getId(), itemId, changeCountForm.getAction()))
                .thenReturn("redirect:/cart/items");
    }

}