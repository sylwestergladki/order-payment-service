package pl.sylwestergladki.order_service.OrderService;


import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sylwestergladki.order_service.Order.Order;
import pl.sylwestergladki.order_service.OrderRepository.OrderRepository;
import pl.sylwestergladki.order_service.dto.OrderResponse;
import pl.sylwestergladki.order_service.kafka.event.OrderCreatedEvent;
import pl.sylwestergladki.order_service.kafka.event.PaymentFailedEvent;
import pl.sylwestergladki.order_service.kafka.event.PaymentSucceededEvent;
import pl.sylwestergladki.order_service.exception.OrderNotFoundException;
import pl.sylwestergladki.order_service.kafka.producer.OrderEventPublisher;

import java.math.BigDecimal;
import java.util.UUID;

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
        String idempotencyKey = UUID.randomUUID().toString();
        publisher.publishOrderCreated(
                new OrderCreatedEvent(
                        order.getId(),
                        order.getAmount(),
                        idempotencyKey)
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

    public void handle(PaymentSucceededEvent event){
        Order order = repository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

        order.markAsPaid();

        repository.save(order);
    }

    public void handle(PaymentFailedEvent event){
        Order order = repository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

        order.markAsFailed();

        repository.save(order);
    }


}
