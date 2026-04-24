package pl.sylwestergladki.order_payment_service.order.OrderService;

import org.springframework.stereotype.Service;
import pl.sylwestergladki.order_payment_service.order.Order.Order;
import pl.sylwestergladki.order_payment_service.order.OrderRepository.OrderRepository;
import pl.sylwestergladki.order_payment_service.order.exception.OrderNotFoundException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public Order createOrder(BigDecimal amount) {
        Order order = Order.create(amount);
        return repository.save(order);
    }

    public Order getOrder(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public void handlePaymentResult(Long orderId, boolean success) {
        Order order = getOrder(orderId);

        if (success) {
            order.markAsPaid();
        } else {
            order.markAsFailed();
        }

        repository.save(order);
    }

    public List<Order> getAll() {
        return repository.findAll();
    }
}
