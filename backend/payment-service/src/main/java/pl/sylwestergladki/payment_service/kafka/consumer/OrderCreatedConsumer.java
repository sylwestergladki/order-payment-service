package pl.sylwestergladki.payment_service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.sylwestergladki.payment_service.PaymentService.PaymentService;
import pl.sylwestergladki.payment_service.kafka.event.OrderCreatedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedConsumer {
    private final PaymentService paymentService;

    @KafkaListener(
            topics = "order-created",
            groupId = "payment-service",
            containerFactory = "orderCreatedKafkaListenerContainerFactory")
    public void handle(OrderCreatedEvent event){
        log.info("Received order-created event: {}", event);
        paymentService.process(event);
    }
}
