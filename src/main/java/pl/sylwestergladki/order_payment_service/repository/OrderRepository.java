package pl.sylwestergladki.order_payment_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sylwestergladki.order_payment_service.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
