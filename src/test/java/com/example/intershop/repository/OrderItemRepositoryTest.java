package com.example.intershop.repository;

import com.example.intershop.IntershopApplicationTests;
import com.example.intershop.model.*;
import com.example.intershop.service.ItemService;
import com.example.intershop.service.OrderItemService;
import com.example.intershop.service.OrderService;
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

    @Test
    public void testCreate() {
        var item = itemService.insert(new Item("title", 1, BigDecimal.valueOf(2.5)));
        var order = orderService.findNewOrder();
        var orderItem = orderItemService.insert(new OrderItem(order.getId(), item.getId(), 1));

        assertThat(orderItem)
                .isNotNull()
                .withFailMessage("Созданной записи должен был быть присвоен ID")
                .extracting(OrderItem::getId)
                .isNotNull();
    }

    @Test
    public void testChangeCount() {
        var item = itemService.insert(new Item("title", 1, BigDecimal.valueOf(2.5)));
        var order = orderService.findNewOrder();
        var orderItem = orderItemService.insert(new OrderItem(order.getId(), item.getId(), 1));

        orderItemService.update(order.getId(), item.getId(), ItemAction.PLUS);
        var incremented = orderItemService.findById(orderItem.getId()).get();
        assertThat(incremented.getCount()).isEqualTo(2);

        orderItemService.update(order.getId(), item.getId(), ItemAction.MINUS);
        var decremented = orderItemService.findById(orderItem.getId()).get();
        assertThat(decremented.getCount()).isEqualTo(1);

        orderItemService.update(order.getId(), item.getId(), ItemAction.DELETE);
        var deleted = orderItemService.findById(orderItem.getId());
        assertThat(deleted.isEmpty()).isEqualTo(true);
    }

    @Test
    public void testDelete() {
        var item = itemService.insert(new Item("title", 1, BigDecimal.valueOf(2.5)));
        var order = orderService.findNewOrder();
        var orderItem = orderItemService.insert(new OrderItem(order.getId(), item.getId(), 1));
        orderItemService.deleteById(orderItem.getId());

        assertThat(orderItemRepository.existsById(orderItem.getId()))
                .withFailMessage("Удалённая запись не должна быть найдена")
                .isFalse();
    }
}