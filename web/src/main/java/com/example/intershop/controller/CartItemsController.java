package com.example.intershop.controller;

import com.example.intershop.api.DefaultApi;
import com.example.intershop.model.*;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/cart/items")
public class CartItemsController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    private final DefaultApi api;

    public CartItemsController(
            OrderService orderService,
            OrderItemService orderItemService,
            DefaultApi api
    ) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.api = api;
    }

    @GetMapping
    public Mono<String> getOrderItems(Model model) {
        return orderService.findNewOrder()
                .flatMap(order -> orderItemService.findByOrderId(order.getId())
                        .collectList()
                        .map(items -> Pair.of(order.getId(), items))
                )
                .flatMap(pair -> api.balanceGetWithHttpInfo()
                        .timeout(Duration.of(5, ChronoUnit.SECONDS))
                        .map(resp -> {
                            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                                return Pair.of(pair, resp.getBody().getBalance());
                            } else {
                                return Pair.of(pair, BigDecimal.ONE.negate());
                            }
                        })
                        .onErrorReturn(Pair.of(pair, BigDecimal.ONE.negate()))
                )
                .map(pair -> {
                    var orderId = pair.getFirst().getFirst();
                    var items = pair.getFirst().getSecond();
                    var balance = pair.getSecond();
                    OrderUi orderUi = new OrderUi(orderId, items);
                    model.addAttribute("items", items);
                    model.addAttribute("total", orderUi.totalSum());
                    model.addAttribute("empty", items.isEmpty());
                    model.addAttribute("balance", balance);
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