package pl.sylwestergladki.payment_service.dto;


import pl.sylwestergladki.payment_service.PaymentStatus.PaymentStatus;

public record PaymentResponse(Long id, Long orderId, PaymentStatus status) {
}
