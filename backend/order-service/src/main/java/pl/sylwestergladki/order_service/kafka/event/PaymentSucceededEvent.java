package pl.sylwestergladki.order_service.kafka.event;

public record PaymentSucceededEvent(Long orderId) {
}
