package pl.sylwestergladki.payment_service.payment.application.port;

public interface PaymentGateway {
    boolean charge();
}
