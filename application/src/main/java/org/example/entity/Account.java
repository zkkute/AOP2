package org.example.entity;

import org.example.enums.AccountStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal frozenAmount;

    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal balance; // Новое поле

    @Column(name = "client_id", nullable = false)
    private String clientId;

    public Account() {}

    public Account(String accountId, AccountStatus status, BigDecimal frozenAmount, BigDecimal balance) {
        this.accountId = accountId;
        this.status = status;
        this.frozenAmount = frozenAmount;
        this.balance = balance;
    }

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public BigDecimal getFrozenAmount() {
        return frozenAmount;
    }

    public void setFrozenAmount(BigDecimal frozenAmount) {
        this.frozenAmount = frozenAmount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getClientId(){
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}