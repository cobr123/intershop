package com.example.intershop.repository;

import com.example.intershop.IntershopApplicationTests;
import com.example.intershop.Transaction;
import com.example.intershop.model.Item;
import com.example.intershop.service.ItemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

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

    @Test
    public void testCreate() {
        itemService.insert(new Item("title", BigDecimal.valueOf(2.5)))
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .assertNext(item -> {
                    assertThat(item)
                            .isNotNull()
                            .withFailMessage("Созданной записи должен был быть присвоен ID")
                            .extracting(Item::getId)
                            .isNotNull();
                })
                .verifyComplete();
    }

    @Test
    public void testDelete() {
        itemService.insert(new Item("title", BigDecimal.valueOf(2.5)))
                .flatMap(item -> itemService.deleteById(item.getId()).thenReturn(item))
                .flatMap(item -> itemRepository.existsById(item.getId()))
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .assertNext(exists -> {
                    assertThat(exists)
                            .withFailMessage("Удалённая запись не должна быть найдена")
                            .isFalse();
                })
                .verifyComplete();


    }
}