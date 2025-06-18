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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@WebMvcTest(CartItemsController.class)
public class CartItemsControllerTest {

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
    public void testCartItemsList() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        doReturn(order).when(orderService).findNewOrder();
        doReturn(List.of(item)).when(orderItemService).findByOrderId(anyLong());

        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("total"))
                .andExpect(model().attributeExists("empty"))
                .andExpect(xpath("//table/tr/td/table/tr/td/img").exists());
    }

    @Test
    public void testCartItemsListAdd() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));

        doReturn(order).when(orderService).findNewOrder();

        mockMvc.perform(post("/cart/items/1").param("action", "Plus"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items"));

        verify(orderItemService).update(anyLong(), anyLong(), any());
    }

}