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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MainItemsController.class)
public class MainItemsControllerTest {

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
    public void testMainItemsList() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item1 = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));
        var item2 = new ItemUi(2L, "title2", "description2", "imgPath2", 2, BigDecimal.valueOf(2.2));
        var paging = new Paging(1, 10, false, false);
        Items items = new Items(ItemUi.grouped(List.of(item1, item2)), paging);

        doReturn(order).when(orderService).findNewOrder();
        doReturn(items).when(orderItemService).findAll(anyLong(), any(), anyInt(), anyInt());

        mockMvc.perform(get("/main/items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("sort"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(xpath("//table/tr/td/table/tr/td/a").exists());
    }

    @Test
    public void testSearchMainItemsList() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item1 = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));
        var item2 = new ItemUi(2L, "title2", "description2", "imgPath2", 2, BigDecimal.valueOf(2.2));
        var paging = new Paging(1, 10, false, false);
        Items items = new Items(ItemUi.grouped(List.of(item1, item2)), paging);

        doReturn(order).when(orderService).findNewOrder();
        doReturn(items).when(orderItemService).findByTitleLikeOrDescriptionLike(anyLong(), any(), any(), anyInt(), anyInt());

        mockMvc.perform(get("/main/items")
                        .param("search", "tl")
                        .param("sort", "Price"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("sort"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(xpath("//table/tr/td/table/tr/td/a").exists());
    }

    @Test
    public void testSearchPaginatedMainItemsList() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        var item1 = new ItemUi(1L, "title1", "description1", "imgPath1", 1, BigDecimal.valueOf(1.1));
        var item2 = new ItemUi(2L, "title2", "description2", "imgPath2", 2, BigDecimal.valueOf(2.2));
        var paging = new Paging(1, 10, false, false);
        Items items = new Items(ItemUi.grouped(List.of(item1, item2)), paging);

        doReturn(order).when(orderService).findNewOrder();
        doReturn(items).when(orderItemService).findAll(anyLong(), any(), anyInt(), anyInt());

        mockMvc.perform(get("/main/items")
                        .param("pageSize", "10")
                        .param("pageNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("sort"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(xpath("//table/tr/td/table/tr/td/a").exists());
    }


    @Test
    public void testMainItemsListAdd() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        doReturn(order).when(orderService).findNewOrder();

        mockMvc.perform(post("/main/items/1").param("action", "PLUS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));

        verify(orderItemService).update(any(), any(), any());
    }

}