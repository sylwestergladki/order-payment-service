package pl.sylwestergladki.order_service.kafka.event;

public record PaymentFailedEvent(Long orderId, String reason) {
}
