package pl.sylwestergladki.order_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.sylwestergladki.order_service.kafka.event.OrderCreatedEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCreated(OrderCreatedEvent event) {
        log.info("Sending event to Kafka topic 'order-created': {}", event);
        kafkaTemplate.send("order-created", event.orderId().toString(), event);
    }
}