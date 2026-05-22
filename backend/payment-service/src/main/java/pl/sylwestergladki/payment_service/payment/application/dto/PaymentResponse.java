package pl.sylwestergladki.payment_service.payment.application.dto;


import pl.sylwestergladki.payment_service.payment.domain.PaymentStatus;

public record PaymentResponse(Long id, Long orderId, PaymentStatus status) {
}
