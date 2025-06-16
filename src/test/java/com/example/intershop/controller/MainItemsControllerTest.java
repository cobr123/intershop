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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
public class MainItemsControllerTest {

    @MockitoBean
    private MainItemsController controller;

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
    public void testSearchMainItemsList() throws Exception {
        mockMvc.perform(get("/main/items")
                        .param("search", "123")
                        .param("sort", "PRICE"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSearchPaginatedMainItemsList() throws Exception {
        mockMvc.perform(get("/main/items")
                        .param("pageSize", "10")
                        .param("pageNumber", "1"))
                .andExpect(status().isOk());
    }


    @Test
    public void testMainItemsListAdd() throws Exception {
        mockMvc.perform(post("/main/items/1").param("action", "PLUS"))
                .andExpect(status().isOk());
    }

}