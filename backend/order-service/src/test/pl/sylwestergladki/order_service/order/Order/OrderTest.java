package pl.sylwestergladki.order_service.order.Order;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pl.sylwestergladki.order_service.Order.Order;
import pl.sylwestergladki.order_service.Order.OrderStatus;
import pl.sylwestergladki.order_service.exception.InvalidOrderAmountException;

import java.math.BigDecimal;

class OrderTest {

    @Test
    void shouldCreateOrder_whenAmountIsValid() {
        // given
        BigDecimal amount = BigDecimal.valueOf(100);

        // when
        Order order = Order.create(amount);

        // then
        assertNotNull(order);
        assertEquals(amount, order.getAmount());
        assertEquals(OrderStatus.NEW, order.getStatus());
    }

    @Test
    void shouldThrowException_whenAmountIsNull() {
        assertThrows(InvalidOrderAmountException.class,
                () -> Order.create(null));
    }

    @Test
    void shouldThrowException_whenAmountIsZero() {
        assertThrows(InvalidOrderAmountException.class,
                () -> Order.create(BigDecimal.ZERO));
    }

    @Test
    void shouldThrowException_whenAmountIsNegative() {
        assertThrows(InvalidOrderAmountException.class,
                () -> Order.create(BigDecimal.valueOf(-10)));
    }

    @Test
    void shouldMarkOrderAsPaid() {
        // given
        Order order = Order.create(BigDecimal.valueOf(100));

        // when
        order.markAsPaid();

        // then
        assertTrue(order.isPaid());
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void shouldMarkOrderAsFailed() {
        // given
        Order order = Order.create(BigDecimal.valueOf(100));

        // when
        order.markAsFailed();

        // then
        assertTrue(order.isFailed());
        assertEquals(OrderStatus.FAILED, order.getStatus());
    }

    @Test
    void shouldReturnFalse_whenOrderIsNotPaid() {
        // given
        Order order = Order.create(BigDecimal.valueOf(100));

        // then
        assertFalse(order.isPaid());
    }

    @Test
    void shouldReturnFalse_whenOrderIsNotFailed() {
        // given
        Order order = Order.create(BigDecimal.valueOf(100));

        // then
        assertFalse(order.isFailed());
    }
}