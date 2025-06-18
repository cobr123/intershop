package com.example.intershop.controller;

import com.example.intershop.model.*;
import com.example.intershop.service.ItemService;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import jakarta.validation.constraints.Min;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/main/items")
public class MainItemsController {

    private final ItemService itemService;
    private final OrderService orderService;
    private final OrderItemService orderItemService;

    public MainItemsController(ItemService itemService, OrderService orderService, OrderItemService orderItemService) {
        this.itemService = itemService;
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public String getAll(
            Model model,
            @RequestParam(required = false, defaultValue = "", name = "search") String search,
            @RequestParam(required = false, defaultValue = "NO", name = "sort") ItemSort itemSort,
            @RequestParam(required = false, defaultValue = "10", name = "pageSize") @Min(1) int pageSize,
            @RequestParam(required = false, defaultValue = "1", name = "pageNumber") @Min(1) int pageNumber
    ) {
        Order order = orderService.findNewOrder();
        Items items;
        if (search.isBlank()) {
            items = orderItemService.findAll(order.getId(), itemSort, pageSize, pageNumber);
        } else {
            items = orderItemService.findByTitleLikeOrDescriptionLike(order.getId(), search, itemSort, pageSize, pageNumber);
        }
        model.addAttribute("search", search);
        model.addAttribute("sort", itemSort.name());
        model.addAttribute("items", items.items());
        model.addAttribute("paging", items.paging());

        return "main";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long itemId, @RequestParam("action") ItemAction action) {
        Order order = orderService.findNewOrder();
        orderItemService.update(order.getId(), itemId, action);

        return "redirect:/main/items";
    }

}