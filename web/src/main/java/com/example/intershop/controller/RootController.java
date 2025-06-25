package com.example.intershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/")
public class RootController {

    @GetMapping
    public Mono<String> rootPage() {
        return Mono.just("redirect:/main/items");
    }

} 