package com.example.intershop.controller;

import com.example.intershop.model.*;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import jakarta.validation.constraints.Min;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/main/items")
public class MainItemsController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    public MainItemsController(OrderService orderService, OrderItemService orderItemService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public Mono<String> getAll(
            Model model,
            @RequestParam(required = false, defaultValue = "", name = "search") String search,
            @RequestParam(required = false, defaultValue = "NO", name = "sort") ItemSort itemSort,
            @RequestParam(required = false, defaultValue = "10", name = "pageSize") @Min(1) int pageSize,
            @RequestParam(required = false, defaultValue = "1", name = "pageNumber") @Min(1) int pageNumber
    ) {
        return orderService.findNewOrder()
                .flatMap(order -> {
                    if (search.isBlank()) {
                        return orderItemService.findAll(order.getId(), itemSort, pageSize, pageNumber);
                    } else {
                        return orderItemService.findByTitleLikeOrDescriptionLike(order.getId(), search, itemSort, pageSize, pageNumber);
                    }
                })
                .map(items -> {
                    model.addAttribute("search", search);
                    model.addAttribute("sort", itemSort.name());
                    model.addAttribute("items", items.items());
                    model.addAttribute("paging", items.paging());

                    return "main";
                });
    }

    @PostMapping("/{id}")
    public Mono<String> update(@PathVariable("id") Long itemId, @RequestParam("action") ItemAction action) {
        return orderService.findNewOrder()
                .flatMap(order -> orderItemService.update(order.getId(), itemId, action))
                .thenReturn("redirect:/main/items");
    }

}