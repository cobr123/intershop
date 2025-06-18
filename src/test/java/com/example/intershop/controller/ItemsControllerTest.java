package com.example.intershop.controller;

import com.example.intershop.model.*;
import com.example.intershop.service.ItemService;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemsController.class)
public class ItemsControllerTest {

    @MockitoBean
    private ItemService itemService;
    @MockitoBean
    private OrderService orderService;
    @MockitoBean
    private OrderItemService orderItemService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        Mockito.reset(itemService);
        Mockito.reset(orderService);
        Mockito.reset(orderItemService);
    }

    @Test
    public void testItemsList() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        doReturn(order).when(orderService).findNewOrder();
        doReturn(Optional.of(item)).when(orderItemService).findByOrderIdAndItemId(anyLong(), anyLong());

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"))
                .andExpect(xpath("//div/p/img").exists());
    }

    @Test
    public void testItemsListAdd() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        doReturn(order).when(orderService).findNewOrder();

        mockMvc.perform(post("/items/1").param("action", "PLUS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/1"));

        verify(orderItemService).update(any(), any(), any());
    }

}