package com.example.intershop.repository;

import com.example.intershop.IntershopApplicationTests;
import com.example.intershop.model.Item;
import com.example.intershop.service.ItemService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(IntershopApplicationTests.class)
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll().block();
    }

    @Test
    public void testCreate() {
        var item = itemService.insert(new Item("title", BigDecimal.valueOf(2.5))).block();

        assertThat(item)
                .isNotNull()
                .withFailMessage("Созданной записи должен был быть присвоен ID")
                .extracting(Item::getId)
                .isNotNull();
    }

    @Test
    public void testDelete() {
        var item = itemService.insert(new Item("title", BigDecimal.valueOf(2.5))).block();
        itemService.deleteById(item.getId()).block();

        assertThat(itemRepository.existsById(item.getId()).block())
                .withFailMessage("Удалённая запись не должна быть найдена")
                .isFalse();
    }
}