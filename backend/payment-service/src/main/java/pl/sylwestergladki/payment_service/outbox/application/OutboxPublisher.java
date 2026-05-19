package pl.sylwestergladki.payment_service.outbox.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.sylwestergladki.payment_service.messaging.event.PaymentFailedEvent;
import pl.sylwestergladki.payment_service.messaging.event.PaymentSucceededEvent;
import pl.sylwestergladki.payment_service.messaging.producer.PaymentEventPublisher;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxEvent;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final PaymentEventPublisher publisher;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publish() {

        List<OutboxEvent> events =
                outboxRepository.findTop100ByPublishedFalseOrderByCreatedAtAsc();

        for (OutboxEvent event : events) {

            try {
                switch (event.getEventType()) {

                    case "PaymentSucceededEvent" -> {
                        PaymentSucceededEvent payload =
                                objectMapper.readValue(event.getPayload(),
                                        PaymentSucceededEvent.class);

                        publisher.publishSuccess(payload);
                    }

                    case "PaymentFailedEvent" -> {
                        PaymentFailedEvent payload =
                                objectMapper.readValue(event.getPayload(),
                                        PaymentFailedEvent.class);

                        publisher.publishFailure(payload);
                    }
                }

                event.markPublished();

            } catch (Exception ex) {
                // log + retry later
            }
        }
    }
}