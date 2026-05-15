package pl.sylwestergladki.payment_service.kafka.event;

import java.time.LocalDateTime;

public record PaymentSucceededEvent(
        Long orderId,
        LocalDateTime successTime
) {
}
