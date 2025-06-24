package com.example.intershop.controller;

import com.example.intershop.model.*;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import jakarta.validation.Valid;
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
    public Mono<String> getAll(Model model, @Valid SearchForm searchForm) {
        return orderService.findNewOrder()
                .flatMap(order -> {
                    if (searchForm.getSearch().isBlank()) {
                        return orderItemService.findAll(order.getId(), searchForm.getItemSort(), searchForm.getPageSize(), searchForm.getPageNumber());
                    } else {
                        return orderItemService.findByTitleLikeOrDescriptionLike(order.getId(), searchForm.getSearch(), searchForm.getItemSort(), searchForm.getPageSize(), searchForm.getPageNumber());
                    }
                })
                .map(items -> {
                    model.addAttribute("search", searchForm.getSearch());
                    model.addAttribute("sort", searchForm.getItemSort().name());
                    model.addAttribute("items", items.items());
                    model.addAttribute("paging", items.paging());

                    return "main";
                });
    }

    @PostMapping("/{id}")
    public Mono<String> update(@PathVariable("id") Long itemId, ChangeCountForm changeCountForm) {
        return orderService.findNewOrder()
                .flatMap(order -> orderItemService.update(order.getId(), itemId, changeCountForm.getAction()))
                .thenReturn("redirect:/main/items");
    }

}