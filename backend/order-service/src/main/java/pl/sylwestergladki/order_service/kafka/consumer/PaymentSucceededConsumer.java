package pl.sylwestergladki.order_service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.sylwestergladki.order_service.OrderService.OrderService;
import pl.sylwestergladki.order_service.kafka.event.PaymentSucceededEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentSucceededConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = "payment-succeeded",
            containerFactory = "paymentSucceededFactory"
    )
    public void handle(PaymentSucceededEvent event) {
        log.info("PAYMENT SUCCEEDED received: {}", event);
        orderService.handle(event);
    }
}
