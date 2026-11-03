package com.sentinelflow.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @RequestHeader("Idempotency-Key")
            @NotBlank String idempotencyKey,

            @Valid @RequestBody CreateOrderRequest request) {

        try {
            Order order = orderService.createOrder(
                    idempotencyKey,
                    request.customerId(),
                    request.amount()
            );

            return new OrderResponse(
                    order.getId(),
                    order.getCustomerId(),
                    order.getAmount(),
                    order.getPaymentStatus().name(),
                    order.getCreatedAt()
            );

        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage()
            );
        }
    }

    public record CreateOrderRequest(
            @NotBlank String customerId,
            @NotNull
            @DecimalMin("0.01")
            BigDecimal amount
    ) {}

    public record OrderResponse(
            UUID orderId,
            String customerId,
            BigDecimal amount,
            String paymentStatus,
            Instant createdAt
    ) {}
}
