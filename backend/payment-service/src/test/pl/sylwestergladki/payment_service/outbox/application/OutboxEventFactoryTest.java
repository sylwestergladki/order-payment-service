package pl.sylwestergladki.payment_service.outbox.application;

import org.junit.jupiter.api.Test;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxEvent;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxStatus;

import static org.junit.jupiter.api.Assertions.*;

class OutboxEventFactoryTest {


    private final OutboxEventFactory outboxEventFactory
            = new OutboxEventFactory();

    @Test
    void shouldCreateNewOutboxEvent() {
        OutboxEvent event = outboxEventFactory.create(
                "PAYMENT",
                "1",
                "payment-failed",
                "{}"
        );

        assertEquals(OutboxStatus.PENDING, event.getStatus());
        assertEquals(0, event.getAttempts());

        assertNotNull(event.getCreatedAt());
        assertNotNull(event.getNextRetryAt());

        assertEquals("payment-failed", event.getEventType());
    }
}