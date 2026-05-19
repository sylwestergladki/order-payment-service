package pl.sylwestergladki.payment_service.messaging.event;

import java.math.BigDecimal;

public record OrderCreatedEvent(
        Long orderId,
        BigDecimal amount,
        String idempotencyKey
) {
}
