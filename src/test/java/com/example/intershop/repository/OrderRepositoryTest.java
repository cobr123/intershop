package com.example.intershop.repository;

import com.example.intershop.IntershopApplicationTests;
import com.example.intershop.model.Order;
import com.example.intershop.model.OrderStatus;
import com.example.intershop.service.OrderService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(IntershopApplicationTests.class)
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService service;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll().block();
    }

    @Test
    public void testCreate() {
        var order = service.findNewOrder().block();

        assertThat(order)
                .isNotNull()
                .withFailMessage("Созданной записи должен был быть присвоен ID")
                .extracting(Order::getId)
                .isNotNull();
    }

    @Test
    public void testFindByStatus() {
        var order = service.insert(new Order(OrderStatus.NEW)).block();
        service.insert(new Order(OrderStatus.GATHERING)).block();
        var foundItems = service.findNewOrder().block();

        assertThat(order)
                .isNotNull()
                .withFailMessage("Созданной записи должен был быть присвоен ID")
                .extracting(Order::getId)
                .isNotNull();

        assertThat(order.getId()).isEqualTo(foundItems.getId());
    }

    @Test
    public void testDelete() {
        var order = service.findNewOrder().block();
        service.deleteById(order.getId()).block();

        assertThat(orderRepository.existsById(order.getId()).block())
                .withFailMessage("Удалённая запись не должна быть найдена")
                .isFalse();
    }
}