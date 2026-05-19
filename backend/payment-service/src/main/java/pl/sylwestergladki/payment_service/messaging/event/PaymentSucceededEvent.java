package pl.sylwestergladki.payment_service.messaging.event;

import java.time.LocalDateTime;

public record PaymentSucceededEvent(
        Long orderId,
        LocalDateTime successTime
) {
}
