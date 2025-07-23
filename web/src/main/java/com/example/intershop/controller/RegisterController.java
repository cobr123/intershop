package com.example.intershop.controller;

import com.example.intershop.model.RegisterUserForm;
import com.example.intershop.model.UserUi;
import com.example.intershop.service.UserService;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Controller
@RequestMapping("/register")
public class RegisterController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public RegisterController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public Mono<String> getForm() {
        return Mono.just("register");
    }

    @PostMapping
    public Mono<String> postForm(Model model, RegisterUserForm form, BindingResult errors) {
        return ReactiveSecurityContextHolder.getContext()
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .map(securityContext -> {
                    if (securityContext.isPresent() && securityContext.get().getAuthentication().isAuthenticated()) {
                        throw new IllegalArgumentException("Вы уже зарегистрированы");
                    }
                    return form.getUsername();
                })
                .flatMap(v -> userService.findByName(form.getUsername()).map(u-> {
                    throw new IllegalArgumentException("Пользователь с таким именем уже существует");
                }).defaultIfEmpty(form.getUsername()))
                .flatMap(name -> {
                    UserDetails newUser = User.withUsername(form.getUsername()).password(form.getPassword()).passwordEncoder(passwordEncoder::encode).build();
                    return userService.insert(new UserUi(form.getUsername(), newUser.getPassword()));
                })
                .map(userUi -> {
                    return "redirect:/";
                })
                .onErrorResume(err -> {
                    if (err.getClass() == IllegalArgumentException.class) {
                        model.addAttribute("registerError", err.getMessage());
                    } else {
                        model.addAttribute("registerError", "Ошибка регистрации, попробуйте позже");
                    }
                    return Mono.just("register");
                });
    }

}