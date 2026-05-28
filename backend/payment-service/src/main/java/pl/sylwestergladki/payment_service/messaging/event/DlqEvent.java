package pl.sylwestergladki.payment_service.messaging.event;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record DlqEvent(
        UUID eventId,
        String aggregateId,
        String originalEventType,
        String payload,
        int attempts,
        String errorMessage,
        String exceptionClass,
        Instant failedAt
) {
}
