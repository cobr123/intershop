package com.example.intershop.controller;

import com.example.intershop.model.Item;
import com.example.intershop.model.ItemAction;
import com.example.intershop.model.Order;
import com.example.intershop.service.ItemService;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/items")
public class ItemsController {

    private final ItemService itemService;
    private final OrderService orderService;
    private final OrderItemService orderItemService;

    public ItemsController(ItemService itemService, OrderService orderService, OrderItemService orderItemService) {
        this.itemService = itemService;
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }

    @GetMapping("/{id}")
    public String getItem(Model model, @PathVariable("id") Long itemId) {
        Item item = itemService.findById(itemId).orElseThrow();
        model.addAttribute("item", item);

        return "item";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long itemId, @PathVariable("action") ItemAction action) {
        Order order = orderService.findNewOrder();
        orderItemService.update(order.getId(), itemId, action);

        return "redirect:/main/items/" + itemId;
    }

}