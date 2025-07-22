package com.example.intershop.repository;

import com.example.intershop.IntershopApplicationTests;
import com.example.intershop.Transaction;
import com.example.intershop.model.Order;
import com.example.intershop.model.OrderStatus;
import com.example.intershop.model.User;
import com.example.intershop.service.OrderService;
import com.example.intershop.service.UserService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(IntershopApplicationTests.class)
public class OrderRepositoryTest {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    CacheManager cacheManager;

    @Before
    public void evictAllCacheValues() {
        for (var cacheName : cacheManager.getCacheNames()) {
            cacheManager.getCache(cacheName).clear();
        }
    }

    @Test
    @WithMockUser
    public void testCreate() {
        userService.insert(new User("name", "")).flatMap(user -> orderService.findNewOrder(user.getId()))
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
        userService.insert(new User("name", "")).flatMap(user -> orderService.insert(new Order(user.getId(), OrderStatus.NEW))
                        .flatMap(order -> orderService.insert(new Order(user.getId(), OrderStatus.GATHERING)).thenReturn(order))
                        .flatMap(order -> orderService.findNewOrder(user.getId()).map(foundOrder -> Pair.of(order, foundOrder))))
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
        userService.insert(new User("name", "")).flatMap(user -> orderService.findNewOrder(user.getId()))
                .flatMap(order -> orderService.deleteById(order.getId()).thenReturn(order))
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