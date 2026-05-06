package pl.sylwestergladki.order_payment_service.order.OrderService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.sylwestergladki.order_payment_service.order.Order.Order;
import pl.sylwestergladki.order_payment_service.order.OrderRepository.OrderRepository;
import pl.sylwestergladki.order_payment_service.order.dto.OrderResponse;
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
        OrderResponse result = service.createOrder(amount);

        // then
        assertNotNull(result);
        verify(repository).save(any(Order.class));
    }

    @Test
    void shouldReturnOrderResponseById() {
        // given
        Order order = Order.create(BigDecimal.valueOf(100));
        OrderResponse orderResponse = new OrderResponse(order.getId(), order.getStatus(), order.getAmount());

        when(repository.findById(1L)).thenReturn(Optional.of(order));

        // when
        OrderResponse result = service.getByIdOrThrow(1L);

        // then
        assertEquals(orderResponse, result);
        verify(repository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        // given
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(OrderNotFoundException.class,
                () -> service.getByIdOrThrow(1L));

        verify(repository).findById(1L);
    }

    @Test
    void shouldReturnAllOrders() {
        // given
        List<Order> orders = List.of(
                Order.create(BigDecimal.valueOf(100)),
                Order.create(BigDecimal.valueOf(200))
        );

        Page<Order> page = new PageImpl<>(orders);

        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        // when
        Page<Order> result = service.getAll(Pageable.unpaged());

        // then
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        verify(repository).findAll(any(Pageable.class));
    }
}