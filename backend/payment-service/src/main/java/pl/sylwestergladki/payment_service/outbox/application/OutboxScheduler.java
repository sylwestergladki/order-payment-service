package pl.sylwestergladki.payment_service.outbox.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.sylwestergladki.payment_service.messaging.producer.PaymentEventPublisher;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private static final int BATCH_SIZE = 50;

    private final OutboxBatchService batchService;
    private final OutboxPublisherService publisherService;

    @Scheduled(fixedDelay = 5000)
    public void processOutbox() {

        List<UUID> eventIds = batchService.lockBatch(BATCH_SIZE);

        if (eventIds.isEmpty()) {
            return;
        }

        log.info(
                "Processing {} outbox events",
                eventIds.size()
        );

        for (UUID eventId : eventIds) {

            try {

                publisherService.publishSingleEvent(eventId);

            } catch (Exception e) {

                log.error(
                        "Unexpected scheduler error for event={}",
                        eventId,
                        e
                );
            }
        }
    }
}