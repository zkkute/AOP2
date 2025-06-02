package org.example.service;

import org.example.entity.Transaction;
import org.example.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    public Transaction createTransaction() {
        Transaction transaction = new Transaction();
        transaction.setStatus(Transaction.Status.REQUESTED);
        return repository.save(transaction);
    }
}