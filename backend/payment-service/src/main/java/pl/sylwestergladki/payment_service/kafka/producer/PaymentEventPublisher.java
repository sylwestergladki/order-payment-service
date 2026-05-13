package pl.sylwestergladki.payment_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pl.sylwestergladki.payment_service.kafka.event.PaymentFailedEvent;
import pl.sylwestergladki.payment_service.kafka.event.PaymentSucceededEvent;


@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishSuccess(PaymentSucceededEvent event){
        log.info("Publishing payment-succeeded event: {}", event);

        kafkaTemplate.send(
                "payment-succeeded",
                event.orderId().toString(),
                event
        );
    }

    public void publishFailure(PaymentFailedEvent event){
        log.info("Publishing payment-failed event: {}", event);

        kafkaTemplate.send(
                "payment-failed",
                event.orderId().toString(),
                event
        );
    }
}
