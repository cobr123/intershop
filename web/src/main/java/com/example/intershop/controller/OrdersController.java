package com.example.intershop.controller;

import com.example.intershop.model.OrderUi;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import com.example.intershop.service.UserService;
import org.springframework.data.util.Pair;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Controller
@RequestMapping("/orders")
public class OrdersController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    private final UserService userService;

    public OrdersController(OrderService orderService, OrderItemService orderItemService, UserService userService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.userService = userService;
    }

    @GetMapping
    public Mono<String> getAll(Model model) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findByName)
                .flux()
                .flatMap(user -> orderService.findAllNotNew(user.getId()))
                .flatMap(order -> orderItemService.findByOrderId(order.getId())
                        .collectList()
                        .map(items -> Pair.of(order.getId(), items))
                )
                .map(pair -> {
                    var orderId = pair.getFirst();
                    var items = pair.getSecond();
                    return new OrderUi(orderId, items);
                })
                .collectList()
                .map(orderUis -> {
                    model.addAttribute("orders", orderUis);
                    return "orders";
                });
    }

    @GetMapping("/{id}")
    public Mono<String> getOrder(Model model, @PathVariable("id") Long id, @RequestParam(value = "newOrder", required = false, defaultValue = "false") Boolean newOrder) {
        return orderService.findById(id)
                .flatMap(order -> orderItemService.findByOrderId(order.getId())
                        .collectList()
                        .map(items -> Pair.of(order.getId(), items))
                )
                .map(pair -> {
                    var orderId = pair.getFirst();
                    var items = pair.getSecond();
                    model.addAttribute("order", new OrderUi(orderId, items));
                    model.addAttribute("newOrder", newOrder);
                    return "order";
                });
    }

}