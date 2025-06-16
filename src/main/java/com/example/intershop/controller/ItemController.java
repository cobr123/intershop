package com.example.intershop.controller;

import com.example.intershop.model.ItemSort;
import com.example.intershop.model.Items;
import com.example.intershop.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/main/items")
public class ItemController {

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping
    public String getAll(
            Model model,
            @RequestParam(required = false, defaultValue = "", name = "search") String search,
            @RequestParam(required = false, defaultValue = "NO", name = "sort") String sort,
            @RequestParam(required = false, defaultValue = "10", name = "pageSize") int pageSize,
            @RequestParam(required = false, defaultValue = "1", name = "pageNumber") int pageNumber
    ) {
        Items items = service.findByTitleLikeOrDescriptionLike(search, ItemSort.valueOf(sort), pageSize, pageNumber, 3);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("items", items.items());
        model.addAttribute("paging", items.paging());

        return "main.html";
    }


}