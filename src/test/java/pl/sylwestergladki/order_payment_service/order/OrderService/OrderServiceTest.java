package pl.sylwestergladki.order_payment_service.order.OrderService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sylwestergladki.order_payment_service.order.Order.Order;
import pl.sylwestergladki.order_payment_service.order.OrderRepository.OrderRepository;
import pl.sylwestergladki.order_payment_service.order.exception.OrderNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    @InjectMocks
    private OrderService service;

    @Test
    void shouldCreateOrder() {
        // given
        BigDecimal amount = BigDecimal.valueOf(100);
        Order order = Order.create(amount);

        when(repository.save(any(Order.class))).thenReturn(order);

        // when
        Order result = service.createOrder(amount);

        // then
        assertNotNull(result);
        verify(repository).save(any(Order.class));
    }

    @Test
    void shouldReturnOrderById() {
        // given
        Order order = Order.create(BigDecimal.valueOf(100));

        when(repository.findById(1L)).thenReturn(Optional.of(order));

        // when
        Order result = service.getOrder(1L);

        // then
        assertEquals(order, result);
        verify(repository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        // given
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(OrderNotFoundException.class,
                () -> service.getOrder(1L));

        verify(repository).findById(1L);
    }

    @Test
    void shouldMarkOrderAsPaid_whenPaymentSuccess() {
        // given
        Order order = Order.create(BigDecimal.valueOf(100));

        when(repository.findById(1L)).thenReturn(Optional.of(order));

        // when
        service.handlePaymentResult(1L, true);

        // then
        assertTrue(order.isPaid());
        verify(repository).save(order);
    }

    @Test
    void shouldMarkOrderAsFailed_whenPaymentFails() {
        // given
        Order order = Order.create(BigDecimal.valueOf(100));

        when(repository.findById(1L)).thenReturn(Optional.of(order));

        // when
        service.handlePaymentResult(1L, false);

        // then
        assertTrue(order.isFailed());
        verify(repository).save(order);
    }

    @Test
    void shouldReturnAllOrders() {
        // given
        List<Order> orders = List.of(
                Order.create(BigDecimal.valueOf(100)),
                Order.create(BigDecimal.valueOf(200))
        );

        when(repository.findAll()).thenReturn(orders);

        // when
        List<Order> result = service.getAll();

        // then
        assertEquals(2, result.size());
        verify(repository).findAll();
    }
}