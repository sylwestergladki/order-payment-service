package pl.sylwestergladki.order_payment_service.service;

import org.springframework.stereotype.Service;
import pl.sylwestergladki.order_payment_service.model.Order;
import pl.sylwestergladki.order_payment_service.model.OrderStatus;
import pl.sylwestergladki.order_payment_service.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public Order createOrder(BigDecimal amount) {
        Order order = new Order();
        order.setAmount(amount);
        order.setStatus(OrderStatus.NEW);
        return repository.save(order);
    }

    public List<Order> getAll() {
        return repository.findAll();
    }
}
