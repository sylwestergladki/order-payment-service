package pl.sylwestergladki.payment_service.kafka.event;

public record PaymentFailedEvent(
        Long orderId,
        String reason
) {
}