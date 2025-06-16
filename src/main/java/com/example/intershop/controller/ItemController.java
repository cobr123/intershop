package com.example.intershop.controller;

import com.example.intershop.model.*;
import com.example.intershop.service.ItemService;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/main/items")
public class ItemController {

    private final ItemService itemService;
    private final OrderService orderService;
    private final OrderItemService orderItemService;

    public ItemController(ItemService itemService, OrderService orderService, OrderItemService orderItemService) {
        this.itemService = itemService;
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public String getAll(
            Model model,
            @RequestParam(required = false, defaultValue = "", name = "search") String search,
            @RequestParam(required = false, defaultValue = "NO", name = "sort") ItemSort itemSort,
            @RequestParam(required = false, defaultValue = "10", name = "pageSize") int pageSize,
            @RequestParam(required = false, defaultValue = "1", name = "pageNumber") int pageNumber
    ) {
        Items items = itemService.findByTitleLikeOrDescriptionLike(search, itemSort, pageSize, pageNumber, 3);
        model.addAttribute("search", search);
        model.addAttribute("sort", itemSort.name());
        model.addAttribute("items", items.items());
        model.addAttribute("paging", items.paging());

        return "main.html";
    }

    @PostMapping(value = "/{id}")
    public String update(@PathVariable("id") Long itemId, @PathVariable("action") ItemAction action) throws IOException {
        Order order = orderService.findNewOrder();
        orderItemService.update(order.getId(), itemId, action);

        return "redirect:/main/items";
    }

}