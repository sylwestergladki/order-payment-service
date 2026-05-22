package pl.sylwestergladki.payment_service.outbox.application;

import org.springframework.stereotype.Component;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxEvent;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxStatus;

import java.time.Instant;

@Component
public class OutboxEventFactory {

    public OutboxEvent create(
            String aggregateType,
            String aggregateId,
            String eventType,
            String payload
    ) {
         Instant now = Instant.now();

        return OutboxEvent.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(payload)
                .status(OutboxStatus.NEW)
                .attempts(0)
                .createdAt(now)
                .nextRetryAt(now)
                .build();
    }
}
