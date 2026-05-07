package pl.sylwestergladki.order_payment_service.order.events;

import java.math.BigDecimal;

public record OrderCreatedEvent(Long orderId, BigDecimal amount) {
}
