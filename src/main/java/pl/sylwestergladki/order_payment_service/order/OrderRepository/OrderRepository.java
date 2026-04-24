package pl.sylwestergladki.order_payment_service.order.OrderRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sylwestergladki.order_payment_service.order.Order.Order;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
