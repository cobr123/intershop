package com.example.intershop.repository;

import com.example.intershop.IntershopApplicationTests;
import com.example.intershop.Transaction;
import com.example.intershop.model.*;
import com.example.intershop.service.ItemService;
import com.example.intershop.service.OrderItemService;
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

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(IntershopApplicationTests.class)
public class OrderItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemService itemService;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderItemService orderItemService;

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
                .flatMap(item -> orderService.findNewOrder().map(order -> Pair.of(item, order)))
                .flatMap(pair -> {
                    Order order = pair.getRight();
                    Item item = pair.getLeft();
                    return orderItemService.insert(new OrderItem(order.getId(), item.getId(), 1));
                })
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .assertNext(orderItem -> {
                    assertThat(orderItem)
                            .isNotNull()
                            .withFailMessage("Созданной записи должен был быть присвоен ID")
                            .extracting(OrderItem::getId)
                            .isNotNull();
                })
                .verifyComplete();
    }

    @Test
    public void testChangeCount() {
        itemService.insert(new Item("title", BigDecimal.valueOf(2.5)))
                .flatMap(item -> orderService.findNewOrder().map(order -> Pair.of(item, order)))
                .flatMap(pair -> {
                    Order order = pair.getRight();
                    Item item = pair.getLeft();
                    return orderItemService.insert(new OrderItem(order.getId(), item.getId(), 1));
                })
                .flatMap(orderItem -> {
                    return orderItemService.update(orderItem.getOrderId(), orderItem.getItemId(), ItemAction.PLUS)
                            .then(orderItemService.findById(orderItem.getId()))
                            .map(incremented -> {
                                assertThat(incremented.getCount()).isEqualTo(2);
                                return incremented;
                            });
                })
                .flatMap(orderItem -> {
                    return orderItemService.update(orderItem.getOrderId(), orderItem.getItemId(), ItemAction.MINUS)
                            .then(orderItemService.findById(orderItem.getId()))
                            .map(decremented -> {
                                assertThat(decremented.getCount()).isEqualTo(1);
                                return decremented;
                            });
                })
                .flatMap(orderItem -> {
                    return orderItemService.update(orderItem.getOrderId(), orderItem.getItemId(), ItemAction.DELETE)
                            .then(orderItemService.existsById(orderItem.getId()))
                            .map(exists -> {
                                assertThat(exists).isEqualTo(false);
                                return orderItem;
                            });
                })
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .assertNext(orderItem -> {
                })
                .verifyComplete();
    }

    @Test
    public void testFindByOrderId() {
        itemService.insert(new Item("title", BigDecimal.valueOf(2.5)))
                .flatMap(item -> orderService.findNewOrder().map(order -> Pair.of(item, order)))
                .flatMap(pair -> {
                    Order order = pair.getRight();
                    Item item = pair.getLeft();
                    return orderItemService.insert(new OrderItem(order.getId(), item.getId(), 1));
                })
                .flatMap(orderItem -> orderItemService.findByOrderId(orderItem.getOrderId()).collectList().map(orderItems -> Pair.of(orderItem, orderItems)))
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .assertNext(pair -> {
                    var orderItem = pair.getLeft();
                    var orderItems = pair.getRight();
                    assertThat(orderItems.size()).isEqualTo(1);
                    assertThat(orderItems.get(0).getId()).isEqualTo(orderItem.getItemId());
                })
                .verifyComplete();
    }

    @Test
    public void testFindByName() {
        var title = UUID.randomUUID().toString();
        itemService.insert(new Item("123" + title + "456", BigDecimal.valueOf(2.5)))
                .flatMap(item -> orderService.findNewOrder().map(order -> Pair.of(order, item)))
                .flatMap(pair -> {
                    Order order = pair.getLeft();
                    Item item = pair.getRight();
                    return orderItemService.findByTitleLikeOrDescriptionLike(order.getId(), title, ItemSort.NO, 10, 1).map(foundItems -> Pair.of(foundItems, item));
                })
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .assertNext(pair -> {
                    Items foundItems = pair.getLeft();
                    Item item = pair.getRight();

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
                })
                .verifyComplete();
    }

    @Test
    public void testFindByNamePaginationBegin() {
        var title = UUID.randomUUID().toString();
        itemService.insert(new Item("1_" + title + "_1", BigDecimal.valueOf(1)))
                .flatMap(item1 -> itemService.insert(new Item("2_" + title + "_2", BigDecimal.valueOf(2))).map(item2 -> Pair.of(item1, item2)))
                .flatMap(pair -> orderService.findNewOrder().map(order -> Pair.of(order, pair)))
                .flatMap(pair -> {
                    Order order = pair.getLeft();
                    return orderItemService.findByTitleLikeOrDescriptionLike(order.getId(), title, ItemSort.NO, 1, 1).map(foundItems -> Pair.of(foundItems, pair.getRight()));
                })
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .assertNext(pair -> {
                    Items foundItems = pair.getLeft();
                    Item item1 = pair.getRight().getLeft();
                    Item item2 = pair.getRight().getRight();

                    assertThat(item1)
                            .isNotNull()
                            .withFailMessage("Созданной записи должен был быть присвоен ID")
                            .extracting(Item::getId)
                            .isNotNull();

                    assertThat(item2)
                            .isNotNull()
                            .withFailMessage("Созданной записи должен был быть присвоен ID")
                            .extracting(Item::getId)
                            .isNotNull();

                    assertThat(foundItems.items().size()).isEqualTo(1);
                    assertThat(foundItems.items().get(0).size()).isEqualTo(1);
                    assertThat(foundItems.items().get(0).get(0).getId()).isEqualTo(item1.getId());
                    assertThat(foundItems.paging().hasNext()).isEqualTo(true);
                    assertThat(foundItems.paging().hasPrevious()).isEqualTo(false);
                })
                .verifyComplete();
    }

    @Test
    public void testFindByNamePaginationEnd() {
        var title = UUID.randomUUID().toString();
        itemService.insert(new Item("1_" + title + "_1", BigDecimal.valueOf(1)))
                .flatMap(item1 -> itemService.insert(new Item("2_" + title + "_2", BigDecimal.valueOf(2))).map(item2 -> Pair.of(item1, item2)))
                .flatMap(pair -> orderService.findNewOrder().map(order -> Pair.of(order, pair)))
                .flatMap(pair -> {
                    Order order = pair.getLeft();
                    return orderItemService.findByTitleLikeOrDescriptionLike(order.getId(), title, ItemSort.NO, 1, 2).map(foundItems -> Pair.of(foundItems, pair.getRight()));
                })
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .assertNext(pair -> {
                    Items foundItems = pair.getLeft();
                    Item item1 = pair.getRight().getLeft();
                    Item item2 = pair.getRight().getRight();

                    assertThat(item1)
                            .isNotNull()
                            .withFailMessage("Созданной записи должен был быть присвоен ID")
                            .extracting(Item::getId)
                            .isNotNull();

                    assertThat(item2)
                            .isNotNull()
                            .withFailMessage("Созданной записи должен был быть присвоен ID")
                            .extracting(Item::getId)
                            .isNotNull();

                    assertThat(foundItems.items().size()).isEqualTo(1);
                    assertThat(foundItems.items().get(0).size()).isEqualTo(1);
                    assertThat(foundItems.items().get(0).get(0).getId()).isEqualTo(item2.getId());
                    assertThat(foundItems.paging().hasNext()).isEqualTo(false);
                    assertThat(foundItems.paging().hasPrevious()).isEqualTo(true);
                })
                .verifyComplete();
    }

    @Test
    public void testFindAll() {
        itemService.insert(new Item("title", BigDecimal.valueOf(2.5)))
                .flatMap(item -> orderService.findNewOrder().map(order -> Pair.of(order, item)))
                .flatMap(pair -> {
                    Order order = pair.getLeft();
                    Item item = pair.getRight();
                    return orderItemService.findAll(order.getId(), ItemSort.NO, 10, 1).map(foundItems -> Pair.of(foundItems, item));
                })
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .assertNext(pair -> {
                    Items foundItems = pair.getLeft();
                    Item item = pair.getRight();

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
                })
                .verifyComplete();
    }

    @Test
    public void testFindAllPaginationBegin() {
        itemService.insert(new Item("title1", BigDecimal.valueOf(1)))
                .flatMap(item1 -> itemService.insert(new Item("title2", BigDecimal.valueOf(2))).map(item2 -> Pair.of(item1, item2)))
                .flatMap(pair -> orderService.findNewOrder().map(order -> Pair.of(order, pair)))
                .flatMap(pair -> {
                    Order order = pair.getLeft();
                    return orderItemService.findAll(order.getId(), ItemSort.NO, 1, 1).map(foundItems -> Pair.of(foundItems, pair.getRight()));
                })
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .assertNext(pair -> {
                    Items foundItems = pair.getLeft();
                    Item item1 = pair.getRight().getLeft();
                    Item item2 = pair.getRight().getRight();

                    assertThat(item1)
                            .isNotNull()
                            .withFailMessage("Созданной записи должен был быть присвоен ID")
                            .extracting(Item::getId)
                            .isNotNull();

                    assertThat(item2)
                            .isNotNull()
                            .withFailMessage("Созданной записи должен был быть присвоен ID")
                            .extracting(Item::getId)
                            .isNotNull();

                    assertThat(foundItems.items().size()).isEqualTo(1);
                    assertThat(foundItems.items().get(0).size()).isEqualTo(1);
                    assertThat(foundItems.items().get(0).get(0).getId()).isEqualTo(item1.getId());
                    assertThat(foundItems.paging().hasNext()).isEqualTo(true);
                    assertThat(foundItems.paging().hasPrevious()).isEqualTo(false);
                })
                .verifyComplete();
    }

    @Test
    public void testFindAllPaginationEnd() {
        itemService.insert(new Item("title1", BigDecimal.valueOf(1)))
                .flatMap(item1 -> itemService.insert(new Item("title2", BigDecimal.valueOf(2))).map(item2 -> Pair.of(item1, item2)))
                .flatMap(pair -> orderService.findNewOrder().map(order -> Pair.of(order, pair)))
                .flatMap(pair -> {
                    Order order = pair.getLeft();
                    return orderItemService.findAll(order.getId(), ItemSort.NO, 1, 2).map(foundItems -> Pair.of(foundItems, pair.getRight()));
                })
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .assertNext(pair -> {
                    Items foundItems = pair.getLeft();
                    Item item1 = pair.getRight().getLeft();
                    Item item2 = pair.getRight().getRight();

                    assertThat(item1)
                            .isNotNull()
                            .withFailMessage("Созданной записи должен был быть присвоен ID")
                            .extracting(Item::getId)
                            .isNotNull();

                    assertThat(item2)
                            .isNotNull()
                            .withFailMessage("Созданной записи должен был быть присвоен ID")
                            .extracting(Item::getId)
                            .isNotNull();

                    assertThat(foundItems.items().size()).isEqualTo(1);
                    assertThat(foundItems.items().get(0).size()).isEqualTo(1);
                    assertThat(foundItems.items().get(0).get(0).getId()).isEqualTo(item2.getId());
                    assertThat(foundItems.paging().hasNext()).isEqualTo(false);
                    assertThat(foundItems.paging().hasPrevious()).isEqualTo(true);
                })
                .verifyComplete();
    }

    @Test
    public void testDelete() {
        itemService.insert(new Item("title", BigDecimal.valueOf(2.5)))
                .flatMap(item -> orderService.findNewOrder().map(order -> Pair.of(order, item)))
                .flatMap(pair -> {
                    Order order = pair.getLeft();
                    Item item = pair.getRight();
                    return orderItemService.insert(new OrderItem(order.getId(), item.getId(), 1));
                })
                .flatMap(orderItem -> orderItemService.deleteById(orderItem.getId()).thenReturn(orderItem))
                .flatMap(orderItem -> orderItemRepository.existsById(orderItem.getId()))
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .assertNext(exists -> {
                    assertThat(exists)
                            .withFailMessage("Удалённая запись не должна быть найдена")
                            .isFalse();
                })
                .verifyComplete();
    }

    @Test
    public void testGetTotalSum() {
        itemService.insert(new Item("title", BigDecimal.valueOf(2.5)))
                .flatMap(item -> orderService.findNewOrder().map(order -> Pair.of(order, item)))
                .flatMap(pair -> {
                    Order order = pair.getLeft();
                    Item item = pair.getRight();
                    return orderItemService.insert(new OrderItem(order.getId(), item.getId(), 2));
                })
                .flatMap(orderItem -> orderItemService.getTotalSumByOrderId(orderItem.getOrderId()))
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .assertNext(sum -> {
                    assertThat(sum).isEqualTo(BigDecimal.valueOf(5.0));
                })
                .verifyComplete();
    }
}