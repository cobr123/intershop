package com.example.intershop.controller;

import com.example.intershop.model.Item;
import com.example.intershop.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
public class ImageControllerTest {

    @MockitoBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        Mockito.reset(itemService);
    }

    @Test
    public void testGetImage() throws Exception {
        File tempFile = File.createTempFile("prefix-", "-suffix");
        tempFile.deleteOnExit();
        var item = new Item(1L, "title1", "description1", tempFile.getAbsolutePath(), BigDecimal.valueOf(1.1));

        doReturn(Optional.of(item)).when(itemService).findById(anyLong());

        mockMvc.perform(get("/images/1"))
                .andExpect(status().isOk());
    }

}