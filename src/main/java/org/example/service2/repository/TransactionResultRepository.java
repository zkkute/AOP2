package org.example.service2.repository;

import org.example.service2.entity.TransactionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;

public interface TransactionResultRepository extends JpaRepository<TransactionResult, Long> {
    // Найти транзакции по accountId и timestamp больше определённого значения
    List<TransactionResult> findByAccountIdAndTimestampAfter(String accountId, Instant timestamp);
}