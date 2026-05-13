package pl.sylwestergladki.payment_service.Payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import pl.sylwestergladki.payment_service.PaymentStatus.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Payment {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String idempotencyKey;
    private Long orderId;
    private PaymentStatus status;
    private BigDecimal amount;
    private LocalDateTime processedAt;

    public static Payment create(Long orderId, BigDecimal amount, String idempotencyKey) {
        Payment payment = new Payment();
        payment.orderId = orderId;
        payment.amount = amount;
        payment.status = PaymentStatus.PENDING;
        payment.idempotencyKey = idempotencyKey;
        return payment;
    }

    public void markSuccess() {
        this.status = PaymentStatus.SUCCESS;
        this.processedAt = LocalDateTime.now();
    }

    public void markFailed() {
        this.status = PaymentStatus.FAILED;
        this.processedAt = LocalDateTime.now();
    }
}
