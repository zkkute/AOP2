package org.example.service3.entity;

import jakarta.persistence.*;
import org.example.service3.enums.AccountStatus;

import java.math.BigDecimal;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", unique = true, nullable = false)
    private String accountId;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status = AccountStatus.OPEN;

    @Column(name = "balance", precision = 19, scale = 4, nullable = false)
    private BigDecimal balance;

    @Column(name = "frozen_amount", precision = 19, scale = 4, nullable = false)
    private BigDecimal frozenAmount;

    // Конструкторы

    public Account() {}

    public Account(String accountId, String clientId, AccountStatus status,
                   BigDecimal balance, BigDecimal frozenAmount) {
        this.accountId = accountId;
        this.clientId = clientId;
        this.status = status;
        this.balance = balance;
        this.frozenAmount = frozenAmount;
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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getFrozenAmount() {
        return frozenAmount;
    }

    public void setFrozenAmount(BigDecimal frozenAmount) {
        this.frozenAmount = frozenAmount;
    }

    // toString()

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountId='" + accountId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", status=" + status +
                ", balance=" + balance +
                ", frozenAmount=" + frozenAmount +
                '}';
    }
}