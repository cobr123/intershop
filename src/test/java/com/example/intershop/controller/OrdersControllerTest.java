package com.example.intershop.controller;

import com.example.intershop.model.*;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdersController.class)
public class OrdersControllerTest {

    @MockitoBean
    private OrderService orderService;
    @MockitoBean
    private OrderItemService orderItemService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        Mockito.reset(orderService);
        Mockito.reset(orderItemService);
    }

    @Test
    public void testOrdersList() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        doReturn(List.of(order)).when(orderService).findAllNotNew();
        doReturn(List.of(item)).when(orderItemService).findByOrderId(anyLong());

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(xpath("//table/tr/td/h2/a").exists());
    }

    @Test
    public void testOrderCard() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        doReturn(Optional.of(order)).when(orderService).findById(anyLong());
        doReturn(List.of(item)).when(orderItemService).findByOrderId(anyLong());

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attributeExists("newOrder"))
                .andExpect(xpath("//table/tr/td/h2").exists());
    }

    @Test
    public void testNewOrderCard() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        doReturn(Optional.of(order)).when(orderService).findById(anyLong());
        doReturn(List.of(item)).when(orderItemService).findByOrderId(anyLong());

        mockMvc.perform(get("/orders/1?newOrder=true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attributeExists("newOrder"))
                .andExpect(xpath("//table/tr/td/h2").exists());
    }

}