package pl.sylwestergladki.payment_service.outbox.application;

public interface EventPublisher {

    void publish(
            String eventType,
            String key,
            String payload
    );
}