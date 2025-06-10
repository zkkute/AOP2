package org.example.service;

import org.example.DTO.IncomingTransactionDto;
import org.example.client.ClientStatusClient;
import org.example.entity.Account;
import org.example.entity.Client;
import org.example.entity.Transaction;
import org.example.enums.AccountStatus;
import org.example.enums.TransactionStatus;
import org.example.kafka.KafkaProducerService;
import org.example.repository.AccountRepository;
import org.example.repository.ClientRepository;
import org.example.repository.TransactionRepository;
import org.example.config.TransactionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionProcessingService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private ClientStatusClient clientStatusClient;

    @Autowired
    private TransactionConfig transactionConfig;

    public void processIncomingTransaction(IncomingTransactionDto dto) {
        // Поиск аккаунта по accountId
        Optional<Account> accountOpt = accountRepository.findByAccountId(dto.getAccountId());

        if (accountOpt.isEmpty()) {
            System.err.println("Account not found: " + dto.getAccountId());
            return;
        }

        Account account = accountOpt.get();

        // Проверяем статус клиента
        AccountStatus clientStatus;

        Optional<Client> clientOpt = clientRepository.findByClientId(dto.getClientId());

        if (clientOpt.isPresent()) {
            clientStatus = clientOpt.get().getStatus();
        } else {
            try {
                String statusStr = clientStatusClient.getClientStatus(dto.getClientId(), dto.getAccountId());
                clientStatus = AccountStatus.valueOf(statusStr.toUpperCase());
            } catch (Exception e) {
                System.err.println("Failed to fetch client status from Service 2: " + e.getMessage());
                return;
            }
        }

        if (!AccountStatus.OPEN.equals(clientStatus)) {
            handleClientBlocked(dto, account, clientStatus);
            return;
        }

        // Проверяем текущий статус счёта
        if (!AccountStatus.OPEN.equals(account.getStatus())) {
            System.out.println("Account is not OPEN. Current status: " + account.getStatus());
            return;
        }

        // Считаем количество REJECTED транзакций у этого счёта
        List<Transaction> rejectedTransactions = transactionRepository
                .findByAccountIdAndStatus(dto.getAccountId(), TransactionStatus.REJECTED);

        if (rejectedTransactions != null &&
                rejectedTransactions.size() >= transactionConfig.getMaxRejectedBeforeArrested()) {

            // Блокируем счёт
            account.setStatus(AccountStatus.ARRESTED);
            accountRepository.save(account);

            // Отклоняем транзакцию
            sendTransactionRejected(dto, "Account arrested due to too many rejected transactions");
            return;
        }

        // Создаем новую транзакцию
        Transaction transaction = new Transaction();
        transaction.setTransactionId(dto.getTransactionId());
        transaction.setStatus(TransactionStatus.REQUESTED);
        transaction.setTimestamp(new Date(dto.getTimestamp()));
        transaction.setAmount(BigDecimal.valueOf(dto.getAmount()));

        transactionRepository.save(transaction);

        // Обновляем баланс
        BigDecimal amount = BigDecimal.valueOf(dto.getAmount());
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        String message = String.format(
                "{clientId: \"%s\", accountId: \"%s\", transactionId: \"%s\", timestamp: %d, amount: %.2f, balance: %.2f}",
                dto.getClientId(),
                dto.getAccountId(),
                dto.getTransactionId(),
                dto.getTimestamp(),
                dto.getAmount(),
                account.getBalance().doubleValue()
        );

        kafkaProducerService.sendMessage("t1_demo_transaction_accept", message, "TRANSACTION_ACCEPT");
    }

    private void handleClientBlocked(IncomingTransactionDto dto, Account account, AccountStatus clientStatus) {
        account.setStatus(AccountStatus.BLOCKED);
        accountRepository.save(account);

        sendTransactionRejected(dto, "Client status: " + clientStatus);
    }

    private void sendTransactionRejected(IncomingTransactionDto dto, String reason) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(dto.getTransactionId());
        transaction.setStatus(TransactionStatus.REJECTED);
        transaction.setTimestamp(new Date(dto.getTimestamp()));
        transaction.setAmount(BigDecimal.valueOf(dto.getAmount()));
        transactionRepository.save(transaction);

        String message = String.format(
                "{ \"accountId\": \"%s\", \"transactionId\": \"%s\", \"status\": \"REJECTED\" }",
                dto.getAccountId(), dto.getTransactionId()
        );
        kafkaProducerService.sendMessage("t1_demo_transaction_result", message, "TRANSACTION_REJECTED");
    }
}