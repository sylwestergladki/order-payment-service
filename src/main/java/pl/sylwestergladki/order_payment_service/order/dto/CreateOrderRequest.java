package pl.sylwestergladki.order_payment_service.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotNull
        @Positive
        BigDecimal amount){}
