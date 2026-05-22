package pl.sylwestergladki.payment_service.messaging.event;

import java.time.Instant;

public record PaymentSucceededEvent(
        Long orderId,
        Instant successTime
) {
}
