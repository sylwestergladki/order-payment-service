package pl.sylwestergladki.payment_service.outbox.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sylwestergladki.payment_service.outbox.config.OutboxRetryProperties;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxEvent;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxRepository;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxStatus;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
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

    @Mock
    private DeadLetterPublisher deadLetterPublisher;

    @Mock
    private OutboxRetryProperties retryProperties;

    private OutboxPublisherService outboxPublisherService;

    private UUID eventID;

    private static final Instant FIXED_INSTANT =
            Instant.parse("2026-01-01T00:00:00Z");

    private static final Clock FIXED_CLOCK =
            Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        eventID = UUID.randomUUID();

        outboxPublisherService = new OutboxPublisherService(
                retryProperties,
                outboxRepository,
                eventPublisher,
                deadLetterPublisher,
                FIXED_CLOCK
        );
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
    void shouldRetryWhenPublishingFails() {
        OutboxEvent event = createEvent();

        when(outboxRepository.findByIdForUpdate(eventID))
            .thenReturn(Optional.of(event));

        doThrow(new RuntimeException("Kafka unavailable"))
                .when(eventPublisher)
                .publish(any(), any(), any());

        outboxPublisherService.publishSingleEvent(eventID);

        verify(eventPublisher).publish(any(), any(), any());
        verify(outboxRepository).findByIdForUpdate(eventID);

        assertEquals(1, event.getAttempts());

        assertEquals("Kafka unavailable", event.getLastError());

        assertEquals(OutboxStatus.PENDING, event.getStatus());

        assertNotNull(event.getNextRetryAt());

        assertTrue(event.getNextRetryAt().isAfter(FIXED_INSTANT));
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

        assertEquals(OutboxStatus.DEAD_LETTER, event.getStatus());

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
        Instant now = Instant.now(FIXED_CLOCK);

        return OutboxEvent.builder()
                .id(eventID)
                .aggregateType("PAYMENT")
                .aggregateId("123")
                .eventType("payment-failed")
                .payload("{\"orderId\":13")
                .status(OutboxStatus.PENDING)
                .attempts(0)
                .createdAt(now)
                .nextRetryAt(now)
                .build();
    }

    @Test
    void shouldPublishToDlqAfterMaxRetries(){
        OutboxEvent event = createEvent();
        event.setAttempts(4);

        when(outboxRepository.findByIdForUpdate(eventID))
            .thenReturn(Optional.of(event));

        doThrow(new RuntimeException("Permanent failure"))
        .when(eventPublisher)
                .publish(any(), any(), any());

        outboxPublisherService.publishSingleEvent(eventID);
        verify(deadLetterPublisher).publish(eq(event), any());

        assertEquals(5, event.getAttempts());
        assertEquals(OutboxStatus.DEAD_LETTER, event.getStatus());
        assertEquals("Permanent failure", event.getLastError());
    }

    @Test
    void shouldNotPublishDlqBeforeMaxRetries(){
        OutboxEvent event = createEvent();
        event.setAttempts(2);

        when(outboxRepository.findByIdForUpdate(eventID))
            .thenReturn(Optional.of(event));

        doThrow(new RuntimeException("Kafka error"))
            .when(eventPublisher)
                .publish(any(), any(), any());

        outboxPublisherService.publishSingleEvent(eventID);

        verify(deadLetterPublisher, never()).publish(eq(event), any());

        assertEquals(3, event.getAttempts());
        assertEquals(OutboxStatus.PENDING, event.getStatus());
    }

    @Test
    void shouldHandleDlqFailureGracefully(){
        OutboxEvent event = createEvent();
        event.setAttempts(4);

        when(outboxRepository.findByIdForUpdate(eventID))
            .thenReturn(Optional.of(event));

        doThrow(new RuntimeException("Kafka down"))
                .when(eventPublisher)
                .publish(any(), any(), any());

        doThrow(new RuntimeException("DLQ failed"))
            .when(deadLetterPublisher).publish(any(), any());

        outboxPublisherService.publishSingleEvent(eventID);

        assertEquals(OutboxStatus.DEAD_LETTER, event.getStatus());
        assertEquals(5, event.getAttempts());
    }
}