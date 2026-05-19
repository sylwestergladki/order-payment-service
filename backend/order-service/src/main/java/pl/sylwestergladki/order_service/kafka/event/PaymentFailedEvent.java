package pl.sylwestergladki.order_service.kafka.event;

import java.time.LocalDateTime;

public record PaymentFailedEvent(
        Long orderId,
        String reason,
        LocalDateTime failedTime
) {
}
