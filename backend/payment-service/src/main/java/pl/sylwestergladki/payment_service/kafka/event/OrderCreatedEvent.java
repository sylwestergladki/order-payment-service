package pl.sylwestergladki.payment_service.kafka.event;

import java.math.BigDecimal;

public record OrderCreatedEvent(
        Long orderId,
        BigDecimal amount,
        String idempotencyKey
) {
}
