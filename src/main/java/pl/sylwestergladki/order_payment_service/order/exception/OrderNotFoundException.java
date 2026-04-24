package pl.sylwestergladki.order_payment_service.order.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super("There is no order with id: " + id);
    }
}
