package pl.sylwestergladki.order_payment_service.order.OrderService;


import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sylwestergladki.order_payment_service.order.Order.Order;
import pl.sylwestergladki.order_payment_service.order.OrderRepository.OrderRepository;
import pl.sylwestergladki.order_payment_service.order.dto.OrderResponse;
import pl.sylwestergladki.order_payment_service.order.events.OrderCreatedEvent;
import pl.sylwestergladki.order_payment_service.order.exception.OrderNotFoundException;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final OrderEventPublisher publisher;

    public OrderService(OrderRepository repository, OrderEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @Transactional
    public OrderResponse createOrder(BigDecimal amount) {
        Order order = Order.create(amount);
        Order savedOrder = repository.save(order);
        publisher.publishOrderCreated(
                new OrderCreatedEvent(order.getId(), order.getAmount())
        );

        return new OrderResponse(savedOrder.getId(), savedOrder.getStatus(), savedOrder.getAmount());
    }

    public Page<Order> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Cacheable(value = "orders", key = "#p0")
    public OrderResponse getByIdOrThrow(Long id) {
        System.out.println("METHOD EXECUTED: " + id);
        Order order =  repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        return new OrderResponse(order.getId(),order.getStatus(), order.getAmount());
    }

}
