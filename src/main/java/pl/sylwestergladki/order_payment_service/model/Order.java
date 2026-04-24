package pl.sylwestergladki.order_payment_service.model;

import jakarta.persistence.*;
import lombok.Data;


import java.math.BigDecimal;

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
}
