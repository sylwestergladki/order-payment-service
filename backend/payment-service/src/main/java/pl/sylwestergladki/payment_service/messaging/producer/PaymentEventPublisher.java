package pl.sylwestergladki.payment_service.messaging.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pl.sylwestergladki.payment_service.outbox.application.EventPublisher;


@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher implements EventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void publish(String eventType, String key, String payload) {

        log.info(
                "Publishing event type={} key={}",
                eventType,
                key
        );

        kafkaTemplate.send(
                eventType,
                key,
                payload
        );
    }

}
