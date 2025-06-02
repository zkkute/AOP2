package org.example.transactionaccept.service;

import org.example.transactionaccept.dto.TransactionAcceptRequest;
import org.example.transactionaccept.entity.TransactionStatusLog;
import org.example.transactionaccept.repository.TransactionStatusLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionAcceptService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private TransactionStatusLogRepository transactionStatusLogRepository;

    @Autowired
    private CacheManager cacheManager;

    @Value("${transaction-limit.count}")
    private int maxTransactions;

    @Value("${transaction-limit.time-window-seconds}")
    private int timeWindowSeconds;

    public void processTransaction(TransactionAcceptRequest request) {
        String cacheKey = request.getClientId() + ":" + request.getAccountId();
        Cache cache = cacheManager.getCache("transactionHistory");

        List<LocalDateTime> timestamps = cache.getIfPresent(cacheKey);
        if (timestamps == null) {
            timestamps = new ArrayList<>();
        }

        timestamps.add(request.getTimestamp());
        cache.put(cacheKey, timestamps);

        // Проверка частых транзакций
        if (timestamps.size() > maxTransactions) {
            sendToKafka(request.getTransactionId(), request.getAccountId(), "BLOCKED");
            logStatus(request, "BLOCKED");
            return;
        }

        // Проверка баланса
        if (request.getAmount().compareTo(request.getBalance()) > 0) {
            sendToKafka(request.getTransactionId(), request.getAccountId(), "REJECTED");
            logStatus(request, "REJECTED");
            return;
        }

        // Все ок
        sendToKafka(request.getTransactionId(), request.getAccountId(), "ACCEPTED");
        logStatus(request, "ACCEPTED");
    }

    private void sendToKafka(String transactionId, String accountId, String status) {
        String message = String.format("{status: \"%s\", accountId: \"%s\", transactionId: \"%s\"}",
                status, accountId, transactionId);
        kafkaTemplate.send("t1_demo_transaction_result", message);
    }

    private void logStatus(TransactionAcceptRequest request, String status) {
        TransactionStatusLog logEntry = new TransactionStatusLog();
        logEntry.setClientId(request.getClientId());
        logEntry.setAccountId(request.getAccountId());
        logEntry.setTransactionId(request.getTransactionId());
        logEntry.setStatus(status);
        logEntry.setTimestamp(LocalDateTime.now());
        transactionStatusLogRepository.save(logEntry);
    }
}