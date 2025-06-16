package com.example.intershop.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
public class OrdersControllerTest {

    @MockitoBean
    private OrdersController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("classpath:templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    public void testOrdersList() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk());
    }

    @Test
    public void testOrderCard() throws Exception {
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testNewOrderCard() throws Exception {
        mockMvc.perform(get("/orders/1?newOrder=true"))
                .andExpect(status().isOk());
    }

}