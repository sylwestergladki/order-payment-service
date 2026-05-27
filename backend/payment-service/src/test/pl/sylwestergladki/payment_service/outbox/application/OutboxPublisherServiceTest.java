package pl.sylwestergladki.payment_service.outbox.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxEvent;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxRepository;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxStatus;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherServiceTest {

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private OutboxPublisherService outboxPublisherService;

    private UUID eventID;

    @BeforeEach
    void setUp() {
        eventID = UUID.randomUUID();
    }

    @Test
    void shouldPublishEventSuccessfully() {
        OutboxEvent event = createEvent();

        when(outboxRepository.findByIdForUpdate(eventID))
                .thenReturn(Optional.of(event));

        outboxPublisherService.publishSingleEvent(eventID);

        verify(eventPublisher).publish(
                event.getEventType(),
                event.getAggregateId(),
                event.getPayload()
        );

        assertEquals(OutboxStatus.PUBLISHED, event.getStatus());

        assertNotNull(event.getPublishedAt());

        assertEquals(0, event.getAttempts());
    }

    @Test
    void shouldRetryWhenPublishingFails(){
        OutboxEvent event = createEvent();

        when(outboxRepository.findByIdForUpdate(eventID))
            .thenReturn(Optional.of(event));

        doThrow(new RuntimeException("Kafka unavailable"))
                .when(eventPublisher)
                .publish(any(), any(), any());

        outboxPublisherService.publishSingleEvent(eventID);

        verify(eventPublisher).publish(any(), any(), any());

        assertEquals(1, event.getAttempts());

        assertEquals("Kafka unavailable", event.getLastError());

        assertEquals(OutboxStatus.NEW, event.getStatus());

        assertNotNull(event.getNextRetryAt());
    }

    @Test
    void shouldMarkEventAsFailedAfterMaxRetries(){
        OutboxEvent event = createEvent();

        event.setAttempts(4);

        when(outboxRepository.findByIdForUpdate(eventID))
                .thenReturn(Optional.of(event));

        doThrow(new RuntimeException("Permanent failure"))
                .when(eventPublisher)
                .publish(any(), any(), any());

        outboxPublisherService.publishSingleEvent(eventID);

        assertEquals(5, event.getAttempts());

        assertEquals(OutboxStatus.FAILED, event.getStatus());

        assertEquals("Permanent failure", event.getLastError());
    }

    @Test
    void shouldIgnoreAlreadyPublishedEvent(){
        OutboxEvent event = createEvent();

        event.setStatus(OutboxStatus.PUBLISHED);

        when(outboxRepository.findByIdForUpdate(eventID))
                .thenReturn(Optional.of(event));

        outboxPublisherService.publishSingleEvent(eventID);

        verifyNoInteractions(eventPublisher);
    }

    @Test
    void shouldThrowWhenEventDoesNotExist(){

        when(outboxRepository.findByIdForUpdate(eventID))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> outboxPublisherService.publishSingleEvent(eventID));

        verifyNoInteractions(eventPublisher);
    }



    private OutboxEvent createEvent() {
        Instant now = Instant.now();

        return OutboxEvent.builder()
                .id(eventID)
                .aggregateType("PAYMENT")
                .aggregateId("123")
                .eventType("payment-failed")
                .payload("{\"orderId\":13")
                .status(OutboxStatus.NEW)
                .attempts(0)
                .createdAt(now)
                .nextRetryAt(now)
                .build();
    }
}