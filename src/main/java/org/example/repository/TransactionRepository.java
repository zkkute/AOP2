package org.example.repository;

import org.example.entity.Transaction;
import org.example.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionId(String transactionId);

    // Новый метод:
    List<Transaction> findByAccountIdAndStatus(String accountId, TransactionStatus status);

}