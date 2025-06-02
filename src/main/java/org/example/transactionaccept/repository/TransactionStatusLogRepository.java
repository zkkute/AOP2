package org.example.transactionaccept.repository;

import org.example.transactionaccept.entity.TransactionStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionStatusLogRepository extends JpaRepository<TransactionStatusLog, Long> {}