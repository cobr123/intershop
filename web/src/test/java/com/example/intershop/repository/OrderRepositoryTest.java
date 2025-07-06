package com.example.intershop.repository;

import com.example.intershop.IntershopApplicationTests;
import com.example.intershop.Transaction;
import com.example.intershop.model.Order;
import com.example.intershop.model.OrderStatus;
import com.example.intershop.service.OrderService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(IntershopApplicationTests.class)
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService service;

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
        service.findNewOrder()
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .assertNext(order -> {
                    assertThat(order)
                            .isNotNull()
                            .withFailMessage("Созданной записи должен был быть присвоен ID")
                            .extracting(Order::getId)
                            .isNotNull();
                })
                .verifyComplete();
    }

    @Test
    public void testFindByStatus() {
        service.insert(new Order(OrderStatus.NEW))
                .flatMap(order -> service.insert(new Order(OrderStatus.GATHERING)).thenReturn(order))
                .flatMap(order -> service.findNewOrder().map(foundOrder -> Pair.of(order, foundOrder)))
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .assertNext(pair -> {
                    var order = pair.getLeft();
                    var foundOrder = pair.getRight();
                    assertThat(order)
                            .isNotNull()
                            .withFailMessage("Созданной записи должен был быть присвоен ID")
                            .extracting(Order::getId)
                            .isNotNull();

                    assertThat(order.getId()).isEqualTo(foundOrder.getId());
                })
                .verifyComplete();
    }

    @Test
    public void testDelete() {
        service.findNewOrder()
                .flatMap(order -> service.deleteById(order.getId()).thenReturn(order))
                .flatMap(order -> orderRepository.existsById(order.getId()))
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