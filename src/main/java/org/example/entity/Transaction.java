package org.example.entity;

import org.example.enums.TransactionStatus;
import jakarta.persistence.*;
import java.util.Date;
import java.math.BigDecimal;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false)
    private Date timestamp;

    // Новое поле: сумма транзакции
    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal amount;

    // Конструкторы

    public Transaction() {}

    public Transaction(String transactionId, TransactionStatus status, Date timestamp, BigDecimal amount) {
        this.transactionId = transactionId;
        this.status = status;
        this.timestamp = timestamp;
        this.amount = amount;
    }

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}