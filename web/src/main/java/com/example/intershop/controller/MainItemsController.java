package com.example.intershop.controller;

import com.example.intershop.model.*;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import com.example.intershop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/main/items")
public class MainItemsController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final UserService userService;

    public MainItemsController(OrderService orderService, OrderItemService orderItemService, UserService userService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.userService = userService;
    }

    @GetMapping
    public Mono<String> getAll(Model model, @Valid SearchForm searchForm) {
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
                .flatMap(orderIdOpt -> {
                    if (searchForm.getSearch().isBlank()) {
                        return orderItemService.findAll(orderIdOpt.orElse(null), searchForm.getItemSort(), searchForm.getPageSize(), searchForm.getPageNumber());
                    } else {
                        return orderItemService.findByTitleLikeOrDescriptionLike(orderIdOpt.orElse(null), searchForm.getSearch(), searchForm.getItemSort(), searchForm.getPageSize(), searchForm.getPageNumber());
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
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findByName)
                .flatMap(user -> orderService.findNewOrder(user.getId()))
                .flatMap(order -> orderItemService.update(order.getId(), itemId, changeCountForm.getAction()))
                .thenReturn("redirect:/main/items");
    }

}