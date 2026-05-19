package pl.sylwestergladki.payment_service.payment.infrastructure.persistance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sylwestergladki.payment_service.payment.domain.Payment;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByIdempotencyKey(String idempotencyKey);
}
