package com.example.intershop.controller;

import com.example.intershop.model.AddItemForm;
import com.example.intershop.model.Item;
import com.example.intershop.model.ItemAction;
import com.example.intershop.service.ItemService;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
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
    public Mono<String> getItem(Model model, @PathVariable("id") Long itemId) {
        return orderService.findNewOrder()
                .flatMap(order -> orderItemService.findByOrderIdAndItemId(order.getId(), itemId))
                .map(itemUi -> model.addAttribute("item", itemUi))
                .thenReturn("item");
    }

    @PostMapping("/{id}")
    public Mono<String> update(@PathVariable("id") Long itemId, @RequestParam("action") ItemAction action) {
        return orderService.findNewOrder()
                .flatMap(order -> orderItemService.update(order.getId(), itemId, action))
                .thenReturn("redirect:/items/" + itemId);
    }

    @GetMapping("/add")
    public Mono<String> getForm() {
        return Mono.just("add-item");
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Mono<String> insert(AddItemForm addItemForm, BindingResult errors) {
        return Mono.fromCallable(() -> {
                    Item item = new Item();

                    if (addItemForm.getImage() != null) {
                        File imageFile = new File(image_dir, UUID.randomUUID().toString());
                        addItemForm.getImage().transferTo(imageFile.toPath());
                        item.setImgPath(imageFile.getAbsolutePath());
                    }

                    item.setTitle(addItemForm.getTitle());
                    item.setDescription(addItemForm.getDescription());
                    item.setPrice(addItemForm.getPrice());
                    return item;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(itemService::insert)
                .map(insertedItem -> "redirect:/items/" + insertedItem.getId());
    }

    @PostMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Mono<String> update(@PathVariable("id") Long id, AddItemForm addItemForm, BindingResult errors) throws IOException {
        return itemService.findById(id)
                .flatMap(item -> Mono.fromCallable(() -> {
                    if (addItemForm.getImage() != null) {
                        if (!item.getImgPath().isEmpty()) {
                            new File(item.getImgPath()).delete();
                        }
                        File imageFile = new File(image_dir, UUID.randomUUID().toString());
                        addItemForm.getImage().transferTo(imageFile.toPath());
                        item.setImgPath(imageFile.getAbsolutePath());
                    }
                    item.setTitle(addItemForm.getTitle());
                    item.setDescription(addItemForm.getDescription());
                    item.setPrice(addItemForm.getPrice());
                    return item;
                }).subscribeOn(Schedulers.boundedElastic()))
                .flatMap(itemService::update)
                .map(updatedItem -> "redirect:/items/" + updatedItem.getId());
    }
}