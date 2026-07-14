package pl.sylwestergladki.payment_service.payment.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sylwestergladki.payment_service.messaging.event.OrderCreatedEvent;
import pl.sylwestergladki.payment_service.messaging.event.PaymentFailedEvent;
import pl.sylwestergladki.payment_service.messaging.event.PaymentSucceededEvent;
import pl.sylwestergladki.payment_service.outbox.application.OutboxEventFactory;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxEvent;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxRepository;
import pl.sylwestergladki.payment_service.payment.application.dto.PaymentResponse;
import pl.sylwestergladki.payment_service.payment.application.exception.PaymentNotFoundException;
import pl.sylwestergladki.payment_service.payment.application.port.PaymentGateway;
import pl.sylwestergladki.payment_service.payment.domain.Payment;
import pl.sylwestergladki.payment_service.payment.infrastructure.observability.metrics.PaymentMetrics;
import pl.sylwestergladki.payment_service.payment.infrastructure.persistance.PaymentRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentGateway gateway;
    private final PaymentMetrics metrics;

    private final OutboxRepository outboxRepository;
    private final OutboxEventFactory outboxEventFactory;

    private final ObjectMapper objectMapper;


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
        Timer.Sample sample = metrics.startProcessingTimer();

        try {
            boolean exists = paymentRepository
                    .existsByIdempotencyKey(event.idempotencyKey());
            if(exists){
                return;
            }
            Payment payment = Payment.create(
                    event.orderId(),
                    event.amount(),
                    event.idempotencyKey()
            );
            paymentRepository.save(payment);

            boolean success = gateway.charge();

            Instant now = Instant.now();

            if(success){
                payment.markSuccess();
                PaymentSucceededEvent integrationEvent =
                        new PaymentSucceededEvent(
                                payment.getOrderId(),
                                now
                        );
                saveOutbox(
                        integrationEvent,
                        payment.getId().toString(),
                        "payment-succeeded"
                );
                metrics.incrementSuccess();
            }else{
                payment.markFailed();
                PaymentFailedEvent integrationEvent =
                        new PaymentFailedEvent(
                                payment.getOrderId(),
                                "Card declined",
                                now
                        );
                saveOutbox(
                        integrationEvent,
                        payment.getId().toString(),
                        "payment-failed"
                );
                metrics.incrementFailed();
            }
        }catch (Exception e){
            metrics.incrementFailed();
            throw e;
        }finally {
            metrics.stopProcessingTimer(sample);
        }
    }


    private void saveOutbox(
            Object event,
            String aggregateId,
            String eventType
    ) {

        try {

            String payload = objectMapper.writeValueAsString(
                    event
            );

            OutboxEvent outboxEvent =
                    outboxEventFactory.create(
                            "PAYMENT",
                            aggregateId,
                            eventType,
                            payload
                    );

            outboxRepository.save(outboxEvent);

        } catch (JsonProcessingException e) {

            throw new RuntimeException(
                    "Failed to serialize outbox event",
                    e
            );
        }
    }

}

