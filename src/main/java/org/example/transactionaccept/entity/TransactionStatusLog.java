package org.example.transactionaccept.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class TransactionStatusLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientId;
    private String accountId;
    private String transactionId;
    private String status; // BLOCKED, REJECTED, ACCEPTED

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}