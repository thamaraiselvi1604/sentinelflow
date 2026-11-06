package com.sentinelflow.order;

import com.sentinelflow.payment.PaymentStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Order createOrder(
            String idempotencyKey,
            String customerId,
            BigDecimal amount) {

        var existing = repository
                .findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
            Order order = existing.get();

            // Do not let the same key represent
            // a different purchase.
            if (!order.getCustomerId().equals(customerId)
                    || order.getAmount().compareTo(amount) != 0) {
                throw new IllegalArgumentException(
                    "Idempotency key reused with different request"
                );
            }

            return order;
        }

        Order order = new Order(
                idempotencyKey,
                customerId,
                amount
        );

        order.setPaymentStatus(PaymentStatus.PENDING);

        return repository.saveAndFlush(order);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrders() {
        return repository.findAll();
    }
}
