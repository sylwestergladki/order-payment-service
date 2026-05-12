package pl.sylwestergladki.order_service.OrderRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sylwestergladki.order_service.Order.Order;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
