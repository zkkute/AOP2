package org.example.service;

import org.example.dto.TransactionRequest;
import org.example.entity.Account;
import org.example.entity.Transaction;
import org.example.repository.AccountRepository;
import org.example.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionProcessingService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "t1_demo_transactions", groupId = "transaction-group")
    @Transactional
    public void processTransaction(TransactionRequest request) {
        // 1. Получить счет по accountId
        Account account = accountRepository.findByAccountId(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // 2. Проверить статус счета
        if (account.getStatus() != Account.Status.OPEN) {
            // Отправить сообщение об ошибке в другой топик
            String errorMessage = String.format("Account %s is not OPEN. Status: %s",
                    request.getAccountId(), account.getStatus());
            kafkaTemplate.send("t1_demo_transaction_errors", errorMessage);
            return;
        }

        // 3. Сохранить транзакцию со статусом REQUESTED
        Transaction transaction = new Transaction();
        transaction.setTransactionId(request.getTransactionId());
        transaction.setStatus(Transaction.Status.REQUESTED);
        transaction.setAccountId(request.getAccountId());
        transaction.setClientId(request.getClientId());
        transaction.setAmount(request.getAmount());
        transaction.setTimestamp(request.getTimestamp());
        transactionRepository.save(transaction);

        // 4. Обновить баланс счета
        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        // 5. Отправить подтверждение в t1_demo_transaction_accept
        String acceptMessage = String.format(
                "{clientId: \"%s\", accountId: \"%s\", transactionId: \"%s\", timestamp: \"%s\", amount: \"%s\", balance: \"%s\"}",
                request.getClientId(),
                request.getAccountId(),
                request.getTransactionId(),
                request.getTimestamp(),
                request.getAmount(),
                account.getBalance()
        );
        kafkaTemplate.send("t1_demo_transaction_accept", acceptMessage);
    }
}