package org.example.transactionaccept.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionAcceptRequest {
    private String clientId;
    private String accountId;
    private String transactionId;
    private LocalDateTime timestamp;
    private BigDecimal amount;
    private BigDecimal balance;
}