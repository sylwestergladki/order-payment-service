package pl.sylwestergladki.order_payment_service.order.events;

public record PaymentFailedEvent(Long orderId, String reason) {
}
