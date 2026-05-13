package pl.sylwestergladki.payment_service.PaymentService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sylwestergladki.payment_service.Payment.Payment;
import pl.sylwestergladki.payment_service.PaymentRepository.PaymentRepository;
import pl.sylwestergladki.payment_service.dto.PaymentResponse;
import pl.sylwestergladki.payment_service.exception.PaymentNotFoundException;
import pl.sylwestergladki.payment_service.kafka.producer.PaymentEventPublisher;
import pl.sylwestergladki.payment_service.kafka.event.OrderCreatedEvent;
import pl.sylwestergladki.payment_service.kafka.event.PaymentFailedEvent;
import pl.sylwestergladki.payment_service.kafka.event.PaymentSucceededEvent;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher publisher;

    public PaymentService(PaymentRepository paymentRepository, PaymentEventPublisher publisher) {
        this.paymentRepository = paymentRepository;
        this.publisher = publisher;
    }

    public Page<Payment> getAll(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }
    public PaymentResponse getByIdOrThrow(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        return new PaymentResponse(payment.getId(),payment.getOrderId(), payment.getStatus());
    }

    @Transactional
    public void process(OrderCreatedEvent event) {
        boolean exists = paymentRepository.existsByIdempotencyKey(
                event.idempotencyKey()
        );

        if(exists){
            return;
        }

        Payment payment = Payment.create(
                event.orderId(),
                event.amount(),
                event.idempotencyKey()
        );

        paymentRepository.save(payment);

        boolean success = simulateGateway();

        if(success){
            payment.markSuccess();
            publisher.publishSuccess(
                    new PaymentSucceededEvent(payment.getOrderId())
            );
        }else{
            payment.markFailed();
            publisher.publishFailure(new PaymentFailedEvent(payment.getOrderId(), "Card declined"));
        }

    }

    private boolean simulateGateway() {
        return true;
    }
}