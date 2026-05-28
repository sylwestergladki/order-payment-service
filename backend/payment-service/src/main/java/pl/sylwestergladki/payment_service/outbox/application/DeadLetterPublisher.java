package pl.sylwestergladki.payment_service.outbox.application;

import pl.sylwestergladki.payment_service.outbox.domain.OutboxEvent;

public interface DeadLetterPublisher {

    void publish(
            OutboxEvent event,
            Exception exception
    );
}
