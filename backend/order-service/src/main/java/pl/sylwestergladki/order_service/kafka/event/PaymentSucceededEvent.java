package pl.sylwestergladki.order_service.kafka.event;

import java.time.LocalDateTime;

public record PaymentSucceededEvent(Long orderId, LocalDateTime successTime) {
}
