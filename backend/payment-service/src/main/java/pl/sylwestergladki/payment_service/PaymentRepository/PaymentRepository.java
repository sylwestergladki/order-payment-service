package pl.sylwestergladki.payment_service.PaymentRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sylwestergladki.payment_service.Payment.Payment;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByIdempotencyKey(String idempotencyKey);
}
