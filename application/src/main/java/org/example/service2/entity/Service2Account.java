package org.example.service2.entity;

import jakarta.persistence.*;
import org.example.enums.AccountStatus;

import java.math.BigDecimal;

@Entity
public class Service2Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountId;

    @Column(name = "client_id")
    private String clientId;

    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    public Service2Account() {}

    public Service2Account(String accountId, BigDecimal balance, AccountStatus status) {
        this.accountId = accountId;
        this.balance = balance;
        this.status = status;
    }

    public Service2Account(String accountId, BigDecimal bigDecimal) {
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }


}