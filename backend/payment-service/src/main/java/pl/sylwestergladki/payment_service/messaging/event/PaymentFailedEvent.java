package pl.sylwestergladki.payment_service.messaging.event;

import java.time.Instant;

public record PaymentFailedEvent(
        Long orderId,
        String reason,
        Instant failedTime
) {
}