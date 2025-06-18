package com.example.intershop.controller;

import com.example.intershop.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
@RequestMapping("/images")
public class ImageController {

    private final ItemService itemService;

    public ImageController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public byte[] get(@PathVariable("id") Long id) throws IOException {
        var item = itemService.findById(id).orElseThrow();
        return Files.readAllBytes(Paths.get(item.getImgPath()));
    }
}
