package com.example.intershop.controller;

import com.example.intershop.model.Item;
import com.example.intershop.model.ItemAction;
import com.example.intershop.model.ItemUi;
import com.example.intershop.model.Order;
import com.example.intershop.service.ItemService;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.UUID;

@Controller
@RequestMapping("/items")
public class ItemsController {

    private final ItemService itemService;
    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @Value("${items.image_dir}")
    private File image_dir;

    public ItemsController(ItemService itemService, OrderService orderService, OrderItemService orderItemService) {
        this.itemService = itemService;
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }

    @GetMapping("/{id}")
    public String getItem(Model model, @PathVariable("id") Long itemId) {
        var order = orderService.findNewOrder();
        ItemUi itemUi = orderItemService.findByOrderIdAndItemId(order.getId(), itemId).orElseThrow();
        model.addAttribute("item", itemUi);

        return "item";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long itemId, @RequestParam("action") ItemAction action) {
        Order order = orderService.findNewOrder();
        orderItemService.update(order.getId(), itemId, action);

        return "redirect:/items/" + itemId;
    }

    @GetMapping("/add")
    public String getForm() {
        return "add-item";
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String insert(@RequestParam("title") String title, @RequestParam("description") String description, @RequestParam("image") MultipartFile image, @RequestParam("price") BigDecimal price) throws IOException {
        Item item = new Item();

        if (image != null) {
            File imageFile = new File(image_dir, UUID.randomUUID().toString());
            Files.write(imageFile.toPath(), image.getBytes());
            item.setImgPath(imageFile.getAbsolutePath());
        }

        item.setTitle(title);
        item.setDescription(description);
        item.setPrice(price);
        Item insertedItem = itemService.insert(item);

        return "redirect:/items/" + insertedItem.getId();
    }

    @PostMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String update(@PathVariable("id") Long id, @RequestParam("title") String title, @RequestParam("description") String description, @RequestParam("image") MultipartFile image, @RequestParam("price") BigDecimal price) throws IOException {
        Item item = itemService.findById(id).orElseThrow();

        if (image != null) {
            if (!item.getImgPath().isEmpty()) {
                new File(item.getImgPath()).delete();
            }
            File imageFile = new File(image_dir, UUID.randomUUID().toString());
            Files.write(imageFile.toPath(), image.getBytes());
            item.setImgPath(imageFile.getAbsolutePath());
        }
        item.setTitle(title);
        item.setDescription(description);
        item.setPrice(price);
        Item updatedItem = itemService.update(item);

        return "redirect:/items/" + updatedItem.getId();
    }
}