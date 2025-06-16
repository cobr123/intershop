package com.example.intershop.repository;

import com.example.intershop.IntershopApplicationTests;
import com.example.intershop.model.Order;
import com.example.intershop.model.OrderStatus;
import com.example.intershop.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(IntershopApplicationTests.class)
@Transactional
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private OrderService service;

    @Test
    public void testCreate() {
        var order = service.findNewOrder();

        assertThat(order)
                .isNotNull()
                .withFailMessage("Созданной записи должен был быть присвоен ID")
                .extracting(Order::getId)
                .isNotNull();
    }

    @Test
    public void testFindByStatus() {
        var order = service.insert(new Order(OrderStatus.NEW));
        service.insert(new Order(OrderStatus.GATHERING));
        var foundItems = service.findNewOrder();

        assertThat(order)
                .isNotNull()
                .withFailMessage("Созданной записи должен был быть присвоен ID")
                .extracting(Order::getId)
                .isNotNull();

        assertThat(order.getId()).isEqualTo(foundItems.getId());
    }

    @Test
    public void testDelete() {
        var order = service.findNewOrder();
        service.deleteById(order.getId());

        assertThat(repository.existsById(order.getId()))
                .withFailMessage("Удалённая запись не должна быть найдена")
                .isFalse();
    }
}