package org.example.service;

import org.example.DTO.IncomingTransactionDto;
import org.example.entity.Account;
import org.example.entity.Transaction;
import org.example.enums.AccountStatus;
import org.example.enums.TransactionStatus;
import org.example.kafka.KafkaProducerService;
import org.example.repository.AccountRepository;
import org.example.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Service
public class TransactionProcessingService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    public void processIncomingTransaction(IncomingTransactionDto dto) {
        // Поиск аккаунта по accountId
        Optional<Account> accountOpt = accountRepository.findByAccountId(dto.getAccountId());

        if (accountOpt.isEmpty()) {
            System.err.println("Account not found: " + dto.getAccountId());
            return;
        }

        Account account = accountOpt.get();

        // Проверка статуса аккаунта
        if (!AccountStatus.OPEN.equals(account.getStatus())) {
            System.out.println("Account is not OPEN. Current status: " + account.getStatus());
            return;
        }

        // Создаем новую транзакцию
        Transaction transaction = new Transaction();
        transaction.setTransactionId(dto.getTransactionId());
        transaction.setStatus(TransactionStatus.REQUESTED);
        transaction.setTimestamp(new Date(dto.getTimestamp()));

        // Сохраняем транзакцию
        transactionRepository.save(transaction);

        // Обновляем баланс
        BigDecimal amount = BigDecimal.valueOf(dto.getAmount());
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        // Формируем сообщение для отправки в Kafka
        String message = String.format(
                "{clientId: \"%s\", accountId: \"%s\", transactionId: \"%s\", timestamp: %d, amount: %.2f, balance: %.2f}",
                dto.getClientId(),
                dto.getAccountId(),
                dto.getTransactionId(),
                dto.getTimestamp(),
                dto.getAmount(),
                account.getBalance().doubleValue()
        );

        // Отправляем сообщение в Kafka
        kafkaProducerService.sendMessage("t1_demo_transaction_accept", message, "TRANSACTION_ACCEPT");
    }
}