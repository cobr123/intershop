package com.example.intershop.controller;

import com.example.intershop.model.AddItemForm;
import com.example.intershop.model.ChangeCountForm;
import com.example.intershop.model.Item;
import com.example.intershop.model.Order;
import com.example.intershop.service.ItemService;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import com.example.intershop.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/items")
public class ItemsController {

    private final ItemService itemService;
    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final UserService userService;

    @Value("${items.image_dir}")
    private File image_dir;

    public ItemsController(ItemService itemService, OrderService orderService, OrderItemService orderItemService, UserService userService) {
        this.itemService = itemService;
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public Mono<String> getItem(Model model, @PathVariable("id") Long itemId) {
        return ReactiveSecurityContextHolder.getContext()
                .map(Optional::of)
                .switchIfEmpty(Mono.just(Optional.empty()))
                .map(securityContextOptional -> securityContextOptional.flatMap(securityContext -> Optional.ofNullable(securityContext.getAuthentication())))
                .flatMap(auth -> {
                    return auth.map(authentication -> userService.findByName(authentication.getName())
                                    .flatMap(user -> orderService.findNewOrder(user.getId()).map(Order::getId))
                                    .map(Optional::of)
                            )
                            .orElseGet(() -> Mono.just(Optional.empty()));
                })
                .flatMap(orderIdOpt -> orderItemService.findByOrderIdAndItemId(orderIdOpt.orElse(null), itemId))
                .map(itemUi -> model.addAttribute("item", itemUi))
                .thenReturn("item");
    }

    @PostMapping("/{id}")
    public Mono<String> update(@PathVariable("id") Long itemId, ChangeCountForm changeCountForm) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findByName)
                .flatMap(user -> orderService.findNewOrder(user.getId()))
                .flatMap(order -> orderItemService.update(order.getId(), itemId, changeCountForm.getAction()))
                .thenReturn("redirect:/items/" + itemId);
    }

    @GetMapping("/add")
    public Mono<String> getForm() {
        return Mono.just("add-item");
    }

    @PostMapping
    public Mono<String> insert(AddItemForm addItemForm, BindingResult errors) {
        return Mono.fromCallable(() -> {
                    Item item = new Item();

                    if (addItemForm.getImage() != null) {
                        File imageFile = new File(image_dir, UUID.randomUUID().toString());
                        addItemForm.getImage().transferTo(imageFile.toPath()).block();
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

}