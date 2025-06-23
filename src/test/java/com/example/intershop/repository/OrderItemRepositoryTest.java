package com.example.intershop.repository;

import com.example.intershop.IntershopApplicationTests;
import com.example.intershop.model.*;
import com.example.intershop.service.ItemService;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

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

    @BeforeEach
    void setUp() {
        orderItemRepository.deleteAll()
                .flatMap(v -> orderRepository.deleteAll())
                .flatMap(v -> itemRepository.deleteAll())
                .block();
    }

    @Test
    public void testCreate() {
        var item = itemService.insert(new Item("title", BigDecimal.valueOf(2.5))).block();
        var order = orderService.findNewOrder().block();
        var orderItem = orderItemService.insert(new OrderItem(order.getId(), item.getId(), 1)).block();

        assertThat(orderItem)
                .isNotNull()
                .withFailMessage("Созданной записи должен был быть присвоен ID")
                .extracting(OrderItem::getId)
                .isNotNull();
    }

    @Test
    public void testChangeCount() {
        var item = itemService.insert(new Item("title", BigDecimal.valueOf(2.5))).block();
        var order = orderService.findNewOrder().block();
        var orderItem = orderItemService.insert(new OrderItem(order.getId(), item.getId(), 1)).block();

        orderItemService.update(order.getId(), item.getId(), ItemAction.PLUS).block();
        var incremented = orderItemService.findById(orderItem.getId()).block();
        assertThat(incremented.getCount()).isEqualTo(2);

        orderItemService.update(order.getId(), item.getId(), ItemAction.MINUS).block();
        var decremented = orderItemService.findById(orderItem.getId()).block();
        assertThat(decremented.getCount()).isEqualTo(1);

        orderItemService.update(order.getId(), item.getId(), ItemAction.DELETE).block();
        var exists = orderItemService.existsById(orderItem.getId()).block();
        assertThat(exists).isEqualTo(false);
    }

    @Test
    public void testFindByOrderId() {
        var item = itemService.insert(new Item("title", BigDecimal.valueOf(2.5))).block();
        var order = orderService.findNewOrder().block();
        var orderItem = orderItemService.insert(new OrderItem(order.getId(), item.getId(), 1)).block();

        var orderItems = orderItemService.findByOrderId(order.getId()).collectList().block();

        assertThat(orderItems.size()).isEqualTo(1);
        assertThat(orderItems.get(0).getId()).isEqualTo(orderItem.getItemId());
    }

    @Test
    public void testFindByName() {
        var title = UUID.randomUUID().toString();
        var item = itemService.insert(new Item("123" + title + "456", BigDecimal.valueOf(2.5))).block();
        var order = orderService.findNewOrder().block();
        var foundItems = orderItemService.findByTitleLikeOrDescriptionLike(order.getId(), title, ItemSort.NO, 10, 1).block();

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
    public void testFindByNamePaginationBegin() {
        var title = UUID.randomUUID().toString();
        var item1 = itemService.insert(new Item("1_" + title + "_1", BigDecimal.valueOf(1))).block();
        var item2 = itemService.insert(new Item("2_" + title + "_2", BigDecimal.valueOf(2))).block();
        var order = orderService.findNewOrder().block();
        var foundItems = orderItemService.findByTitleLikeOrDescriptionLike(order.getId(), title, ItemSort.NO, 1, 1).block();

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
    }

    @Test
    public void testFindByNamePaginationEnd() {
        var title = UUID.randomUUID().toString();
        var item1 = itemService.insert(new Item("1_" + title + "_1", BigDecimal.valueOf(1))).block();
        var item2 = itemService.insert(new Item("2_" + title + "_2", BigDecimal.valueOf(2))).block();
        var order = orderService.findNewOrder().block();
        var foundItems = orderItemService.findByTitleLikeOrDescriptionLike(order.getId(), title, ItemSort.NO, 1, 2).block();

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
    }

    @Test
    public void testFindAll() {
        var item = itemService.insert(new Item("title", BigDecimal.valueOf(2.5))).block();
        var order = orderService.findNewOrder().block();
        var foundItems = orderItemService.findAll(order.getId(), ItemSort.NO, 10, 1).block();

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
    public void testFindAllPaginationBegin() {
        var item1 = itemService.insert(new Item("title1", BigDecimal.valueOf(1))).block();
        var item2 = itemService.insert(new Item("title2", BigDecimal.valueOf(2))).block();
        var order = orderService.findNewOrder().block();
        var foundItems = orderItemService.findAll(order.getId(), ItemSort.NO, 1, 1).block();

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
    }

    @Test
    public void testFindAllPaginationEnd() {
        var item1 = itemService.insert(new Item("title1", BigDecimal.valueOf(1))).block();
        var item2 = itemService.insert(new Item("title2", BigDecimal.valueOf(2))).block();
        var order = orderService.findNewOrder().block();
        var foundItems = orderItemService.findAll(order.getId(), ItemSort.NO, 1, 2).block();

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
    }

    @Test
    public void testDelete() {
        var item = itemService.insert(new Item("title", BigDecimal.valueOf(2.5))).block();
        var order = orderService.findNewOrder().block();
        var orderItem = orderItemService.insert(new OrderItem(order.getId(), item.getId(), 1)).block();
        orderItemService.deleteById(orderItem.getId()).block();

        assertThat(orderItemRepository.existsById(orderItem.getId()).block())
                .withFailMessage("Удалённая запись не должна быть найдена")
                .isFalse();
    }
}