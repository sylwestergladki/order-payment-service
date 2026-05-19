package pl.sylwestergladki.payment_service.messaging.event;

import java.time.LocalDateTime;

public record PaymentFailedEvent(
        Long orderId,
        String reason,
        LocalDateTime failedTime
) {
}