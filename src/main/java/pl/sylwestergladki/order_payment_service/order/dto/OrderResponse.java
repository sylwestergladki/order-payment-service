package pl.sylwestergladki.order_payment_service.order.dto;

import pl.sylwestergladki.order_payment_service.order.Order.OrderStatus;

import java.math.BigDecimal;

public record OrderResponse(Long id, OrderStatus status, BigDecimal amount) {}