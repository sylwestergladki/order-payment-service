package pl.sylwestergladki.order_service.exception;

import java.math.BigDecimal;

public class InvalidOrderAmountException extends RuntimeException {
    public InvalidOrderAmountException(BigDecimal amount) {
        super("Invalid order amount: " + amount);
    }
}
