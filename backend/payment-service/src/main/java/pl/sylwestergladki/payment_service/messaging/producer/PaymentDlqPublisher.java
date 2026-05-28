package pl.sylwestergladki.payment_service.messaging.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pl.sylwestergladki.payment_service.messaging.event.DlqEvent;
import pl.sylwestergladki.payment_service.outbox.application.DeadLetterPublisher;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxEvent;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class PaymentDlqPublisher  implements DeadLetterPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;


    @Override
    public void publish(OutboxEvent event, Exception exception) {
        DlqEvent dlqEvent = DlqEvent.builder()
                .eventId(event.getId())
                .aggregateId(event.getAggregateId())
                .originalEventType(event.getEventType())
                .payload(event.getPayload())
                .attempts(event.getAttempts())
                .errorMessage(exception.getMessage())
                .exceptionClass(exception.getClass().getName())
                .failedAt(Instant.now())
                .build();

        kafkaTemplate.send(
                "payment-events-dlq",
                event.getAggregateId(),
                dlqEvent.toString()
        );
    }
}
