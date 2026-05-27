package pl.sylwestergladki.payment_service.payment.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void shouldCreatePaymentInPendingState() {
        Payment payment = Payment.create(
                1L,
                BigDecimal.valueOf(100),
                "idem-key"
        );

        assertNull(payment.getId());
        assertEquals(1L, payment.getOrderId());
        assertEquals(BigDecimal.valueOf(100), payment.getAmount());
        assertEquals("idem-key", payment.getIdempotencyKey());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertNull(payment.getProcessedAt());
    }

    @Test
    void shouldMarkPaymentAsSuccess() {
        Payment payment = Payment.create(1L, BigDecimal.TEN, "key");

        payment.markSuccess();

        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertNotNull(payment.getProcessedAt());
    }

    @Test
    void shouldMarkPaymentAsFailed() {
        Payment payment = Payment.create(1L, BigDecimal.TEN, "key");

        payment.markFailed();

        assertEquals(PaymentStatus.FAILED, payment.getStatus());
        assertNotNull(payment.getProcessedAt());
    }
}
