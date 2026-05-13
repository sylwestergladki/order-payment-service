package pl.sylwestergladki.payment_service.kafka.event;

public record PaymentSucceededEvent(
        Long orderId
) {
}
