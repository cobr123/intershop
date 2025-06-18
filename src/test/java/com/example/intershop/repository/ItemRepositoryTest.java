package com.example.intershop.repository;

import com.example.intershop.IntershopApplicationTests;
import com.example.intershop.model.Item;
import com.example.intershop.service.ItemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(IntershopApplicationTests.class)
@Transactional
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @Test
    public void testCreate() {
        var item = itemService.insert(new Item("title", BigDecimal.valueOf(2.5)));

        assertThat(item)
                .isNotNull()
                .withFailMessage("Созданной записи должен был быть присвоен ID")
                .extracting(Item::getId)
                .isNotNull();
    }

    @Test
    public void testDelete() {
        var item = itemService.insert(new Item("title", BigDecimal.valueOf(2.5)));
        itemService.deleteById(item.getId());

        assertThat(itemRepository.existsById(item.getId()))
                .withFailMessage("Удалённая запись не должна быть найдена")
                .isFalse();
    }
}