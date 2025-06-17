package com.example.intershop.controller;

import com.example.intershop.IntershopApplicationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(IntershopApplicationTests.class)
public class ItemsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testItemsList() throws Exception {
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"))
                .andExpect(xpath("//div/p/img").exists());
    }

    @Test
    public void testItemsListAdd() throws Exception {
        mockMvc.perform(post("/items/1").param("action", "PLUS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/1"));
    }

}