package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", unique = true, nullable = false)
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "frozen_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal frozenAmount;

    @Column(name = "balance", precision = 19, scale = 2, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO; // НЕ ХВАТАЛО!

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Account() {
        this.accountId = java.util.UUID.randomUUID().toString();
        this.status = Status.OPEN;
        this.frozenAmount = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
    }

    public enum Status {
        ARRESTED, BLOCKED, CLOSED, OPEN
    }
}