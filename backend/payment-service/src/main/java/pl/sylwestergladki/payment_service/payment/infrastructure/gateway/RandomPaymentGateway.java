package pl.sylwestergladki.payment_service.payment.infrastructure.gateway;

import org.springframework.stereotype.Component;
import pl.sylwestergladki.payment_service.payment.application.port.PaymentGateway;

import java.util.Random;

@Component
public class RandomPaymentGateway implements PaymentGateway {

    private final Random random = new Random();

    @Override
    public boolean charge() {
        return random.nextBoolean();
    }
}