package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", unique = true, nullable = false)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column(name = "client_id") // НОВОЕ
    private String clientId;

    @Column(name = "account_id") // НОВОЕ
    private String accountId;

    @Column(name = "amount", precision = 19, scale = 2) // НОВОЕ
    private BigDecimal amount;

    public Transaction() {
        this.transactionId = java.util.UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }

    public enum Status {
        ACCEPTED, REJECTED, BLOCKED, CANCELLED, REQUESTED
    }
}