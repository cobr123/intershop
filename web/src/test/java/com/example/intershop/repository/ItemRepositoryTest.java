package com.example.intershop.repository;

import com.example.intershop.IntershopApplicationTests;
import com.example.intershop.Transaction;
import com.example.intershop.model.Item;
import com.example.intershop.service.ItemService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(IntershopApplicationTests.class)
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    CacheManager cacheManager;

    @Before
    public void evictAllCacheValues() {
        for (var cacheName : cacheManager.getCacheNames()) {
            cacheManager.getCache(cacheName).clear();
        }
    }

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
                .flatMap(item -> itemRepository.existsById(item.getId())
                        .zipWith(itemService.findById(item.getId())
                                .map(Optional::of)
                                .defaultIfEmpty(Optional.empty())))
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .assertNext(pair -> {
                    assertThat(pair.getT1())
                            .withFailMessage("Удалённая запись не должна быть найдена")
                            .isFalse();
                    assertThat(pair.getT2().isEmpty())
                            .withFailMessage("Удалённая запись не должна быть найдена в кеше")
                            .isTrue();
                })
                .verifyComplete();
    }
}