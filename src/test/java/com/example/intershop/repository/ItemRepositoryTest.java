package com.example.intershop.repository;

import com.example.intershop.IntershopApplicationTests;
import com.example.intershop.model.Item;
import com.example.intershop.model.ItemSort;
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
        var item = itemService.insert(new Item("title",1, BigDecimal.valueOf(2.5)));

        assertThat(item)
                .isNotNull()
                .withFailMessage("Созданной записи должен был быть присвоен ID")
                .extracting(Item::getId)
                .isNotNull();
    }

    @Test
    public void testFindByName() {
        var item = itemService.insert(new Item("title",1, BigDecimal.valueOf(2.5)));
        var foundItems = itemService.findByTitleLikeOrDescriptionLike("tl", ItemSort.NO,10,1, 3);

        assertThat(item)
                .isNotNull()
                .withFailMessage("Созданной записи должен был быть присвоен ID")
                .extracting(Item::getId)
                .isNotNull();

        assertThat(foundItems.items().size()).isEqualTo(1);
        assertThat(foundItems.items().get(0).size()).isEqualTo(1);
        assertThat(foundItems.items().get(0).get(0).getId()).isEqualTo(item.getId());
        assertThat(foundItems.paging().hasNext()).isEqualTo(false);
        assertThat(foundItems.paging().hasPrevious()).isEqualTo(false);
    }

    @Test
    public void testDelete() {
        var item = itemService.insert(new Item("title",1, BigDecimal.valueOf(2.5)));
        itemService.deleteById(item.getId());

        assertThat(itemRepository.existsById(item.getId()))
                .withFailMessage("Удалённая запись не должна быть найдена")
                .isFalse();
    }
}