package pl.sylwestergladki.payment_service.outbox.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.sylwestergladki.payment_service.outbox.config.OutboxRetryProperties;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxEvent;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxRepository;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxStatus;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxPublisherService {

    private final OutboxRetryProperties retryProperties;
    private final OutboxRepository repository;
    private final EventPublisher eventPublisher;
    private final DeadLetterPublisher deadLetterPublisher;
    private final Clock clock;

    @Transactional
    public void publishSingleEvent(UUID eventId) {

        OutboxEvent event = repository.findByIdForUpdate(eventId)
                .orElseThrow();

        if (event.getStatus() == OutboxStatus.PUBLISHED) {
            return;
        }

        try {

            eventPublisher.publish(
                    event.getEventType(),
                    event.getAggregateId(),
                    event.getPayload()
            );

            event.setStatus(OutboxStatus.PUBLISHED);
            event.setPublishedAt(Instant.now(clock));

            log.info(
                    "Published outbox event id={} type={}",
                    event.getId(),
                    event.getEventType()
            );

        } catch (Exception e) {

            int attempts = event.getAttempts() + 1;

            event.setAttempts(attempts);
            event.setLastError(e.getMessage());

            if (attempts >= retryProperties.getMaxRetries()) {
                event.setStatus(OutboxStatus.DEAD_LETTER);
                try{
                    deadLetterPublisher.publish(event, e);
                }catch (Exception dlqException) {
                    log.error("Failed to publish DLQ event id={}", event.getId(), dlqException);
                }
            } else {

                event.setNextRetryAt(
                        calculateNextRetry(attempts)
                );
            }

            log.error(
                    "Failed publishing outbox event id={} attempt={}",
                    event.getId(),
                    attempts,
                    e
            );
        }
    }

    private Instant calculateNextRetry(int attempts){
        long exponentialDelay =
                (long) (retryProperties.getBaseDelaySeconds() * Math.pow(2, attempts-1));
        long cappedDelay =
                Math.min(exponentialDelay, retryProperties.getMaxRetries());

        double jitter =
                ThreadLocalRandom.current()
                        .nextDouble(1 - retryProperties.getJitterFactor(),
                                1 + retryProperties.getJitterFactor());

        long finalDelay = (long) (cappedDelay * jitter);

        return Instant.now(clock).plusSeconds(finalDelay);
    }
}
