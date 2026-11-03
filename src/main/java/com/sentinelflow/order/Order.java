package com.sentinelflow.order;

import com.sentinelflow.payment.PaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "orders",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_order_idempotency",
            columnNames = {"idempotency_key"}
        )
    }
)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "idempotency_key",
            nullable = false,
            updatable = false,
            length = 100)
    private String idempotencyKey;

    @Column(nullable = false, length = 100)
    private String customerId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Order() {
    }

    public Order(String idempotencyKey,
                 String customerId,
                 BigDecimal amount) {
        this.idempotencyKey = idempotencyKey;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentStatus = PaymentStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setPaymentStatus(PaymentStatus status) {
        this.paymentStatus = status;
    }
}
