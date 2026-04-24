package pl.sylwestergladki.order_payment_service.order.Order;

import jakarta.persistence.*;
import lombok.Data;
import pl.sylwestergladki.order_payment_service.order.exception.InvalidOrderAmountException;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "customer_order")
public class Order {
    @Id
    @GeneratedValue
    private Long id;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime failedAt;

    public static Order create(BigDecimal amount){
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOrderAmountException(amount);
        }
        Order order = new Order();
        order.amount = amount;
        order.status = OrderStatus.NEW;
        return order;
    }

    public void markAsPaid() {
        this.status = OrderStatus.PAID;
    }

    public void markAsFailed() {
        this.status = OrderStatus.FAILED;
    }

    public boolean isPaid() {
        return this.status.equals(OrderStatus.PAID);
    }

    public boolean isFailed() {
        return this.status.equals(OrderStatus.FAILED);
    }
}
