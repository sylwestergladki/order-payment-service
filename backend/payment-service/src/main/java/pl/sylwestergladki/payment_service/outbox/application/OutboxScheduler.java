package pl.sylwestergladki.payment_service.outbox.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private static final int BATCH_SIZE = 50;

    private final OutboxBatchService batchService;
    private final OutboxPublisherService publisherService;
    private final ExecutorService outboxExecutor;

    @Scheduled(fixedDelay = 5000)
    public void processOutbox() {

        long start = System.currentTimeMillis();
        List<UUID> eventIds = batchService.fetchLockedBatch(BATCH_SIZE);

        if (eventIds.isEmpty()) {
            log.debug("Outbox empty");
            return;
        }

        log.info(
                "Processing {} outbox events",
                eventIds.size()
        );


        for(UUID eventId : eventIds) {
            outboxExecutor.submit(() ->{
               try {
                   publisherService.publishSingleEvent(eventId);
               }catch (Exception e) {
                   log.error("Failed event {}", eventId, e);
               }
            });
        }

        log.info(
                "Submitted {} events in {} ms",
                eventIds.size(),
                System.currentTimeMillis() - start
        );
    }

}