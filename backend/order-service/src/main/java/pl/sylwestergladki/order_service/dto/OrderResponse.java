package pl.sylwestergladki.order_service.dto;

import pl.sylwestergladki.order_service.Order.OrderStatus;

import java.math.BigDecimal;

public record OrderResponse(Long id, OrderStatus status, BigDecimal amount) {}