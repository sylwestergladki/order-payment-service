package pl.sylwestergladki.order_service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.sylwestergladki.order_service.OrderService.OrderService;
import pl.sylwestergladki.order_service.kafka.event.PaymentFailedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentFailedConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = "payment-failed",
            containerFactory = "paymentFailedFactory"
    )
    public void handle(PaymentFailedEvent event) {
        log.info("PAYMENT FAILED received: {}", event);
        log.warn("Reason: {}", event.reason());
        orderService.handle(event);
    }
}
