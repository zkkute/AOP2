package org.example.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.enums.AccountStatus;
import org.example.enums.TransactionStatus;
import org.example.DTO.TransactionResultDto;
import org.example.entity.Account;
import org.example.entity.Transaction;
import org.example.repository.AccountRepository;
import org.example.repository.TransactionRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class TransactionResultConsumer {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionResultConsumer(TransactionRepository transactionRepository,
                                     AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @KafkaListener(topics = "t1_demo_transaction_result", groupId = "transaction-result-group")
    @Transactional
    public void consume(String message) {
        try {
            TransactionResultDto dto = parseJson(message);
            process(dto);
        } catch (Exception e) {
            System.err.println("Error processing transaction result: " + message + ", error: " + e.getMessage());
        }
    }

    private void process(TransactionResultDto dto) {
        String transactionId = dto.getTransactionId();
        String accountId = dto.getAccountId();
        String statusStr = dto.getStatus();

        // Преобразуем статус транзакции
        TransactionStatus transactionStatus;
        try {
            transactionStatus = TransactionStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            System.err.println("Unknown transaction status: " + statusStr);
            return;
        }

        // Найти транзакцию по ID
        transactionRepository.findByTransactionId(transactionId)
                .ifPresent(transaction -> {
                    transaction.setStatus(transactionStatus);
                    transactionRepository.save(transaction);

                    // Обработать логику в зависимости от статуса
                    switch (transactionStatus) {
                        case ACCEPTED:
                            handleAccepted(transaction);
                            break;
                        case BLOCKED:
                            handleBlocked(accountId, transaction.getAmount());
                            break;
                        case REJECTED:
                            handleRejected(accountId, transaction.getAmount());
                            break;
                        default:
                            System.out.println("Unknown status: " + transactionStatus);
                    }
                });
    }

    private void handleAccepted(Transaction transaction) {
        // Ничего не делаем со счётом, только статус транзакции уже обновлён
    }

    private void handleBlocked(String accountId, BigDecimal amount) {
        accountRepository.findByAccountId(accountId).ifPresent(account -> {
            account.setStatus(AccountStatus.BLOCKED);

            BigDecimal currentFrozen = account.getFrozenAmount() != null ? account.getFrozenAmount() : BigDecimal.ZERO;
            account.setFrozenAmount(currentFrozen.add(amount));

            accountRepository.save(account);
        });
    }

    private void handleRejected(String accountId, BigDecimal amount) {
        accountRepository.findByAccountId(accountId).ifPresent(account -> {
            account.setBalance(account.getBalance().subtract(amount));
            accountRepository.save(account);
        });
    }

    private TransactionResultDto parseJson(String json) throws Exception {
        return new ObjectMapper().readValue(json, TransactionResultDto.class);
    }
}