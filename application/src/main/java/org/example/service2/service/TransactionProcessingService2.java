package org.example.service2.service;

import org.example.service2.config.TransactionLimitConfig;
import org.example.service2.dto.TransactionAcceptDto;
import org.example.service2.entity.Service2Account;
import org.example.service2.entity.TransactionResult;
import org.example.service2.repository.AccountRepository;
import org.example.service2.repository.TransactionResultRepository;
import org.example.starter.service.kafka.KafkaProducerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class TransactionProcessingService2 {

    private final AccountRepository accountRepository;
    private final TransactionResultRepository resultRepository;
    private final KafkaProducerService producerService;
    private final TransactionLimitConfig limitConfig;

    public TransactionProcessingService2(AccountRepository accountRepository,
                                         TransactionResultRepository resultRepository,
                                         KafkaProducerService producerService,
                                         TransactionLimitConfig limitConfig) {
        this.accountRepository = accountRepository;
        this.resultRepository = resultRepository;
        this.producerService = producerService;
        this.limitConfig = limitConfig;
    }

    @Transactional
    public void process(TransactionAcceptDto dto) {
        String accountId = dto.getAccountId();
        String transactionId = dto.getTransactionId();
        double amount = dto.getAmount();
        double balance = dto.getBalance();

        Service2Account account = accountRepository.findByAccountId(accountId)
                .orElseGet(() -> new Service2Account(accountId, BigDecimal.valueOf(balance)));

        account.setBalance(BigDecimal.valueOf(balance));
        accountRepository.save(account);

        if (amount > balance) {
            sendStatus(transactionId, accountId, "REJECTED");
            return;
        }

        Instant now = Instant.ofEpochMilli(dto.getTimestamp());
        List<TransactionResult> recentTransactions = resultRepository.findByAccountIdAndTimestampAfter(accountId, now.minusSeconds(limitConfig.getPeriodSeconds()));

        if (recentTransactions.size() >= limitConfig.getMaxPerPeriod()) {
            sendStatus(transactionId, accountId, "BLOCKED");
        } else {
            sendStatus(transactionId, accountId, "ACCEPTED");
        }

        TransactionResult result = new TransactionResult();
        result.setAccountId(accountId);
        result.setTransactionId(transactionId);
        result.setStatus(result.getStatus());
        result.setTimestamp(now);
        resultRepository.save(result);
    }

    private void sendStatus(String transactionId, String accountId, String status) {
        String message = String.format(
                "{ \"accountId\": \"%s\", \"transactionId\": \"%s\", \"status\": \"%s\" }",
                accountId, transactionId, status
        );
        producerService.sendMessage("t1_demo_transaction_result", message, "TRANSACTION_RESULT");
    }
}