package org.example.kafka;

import org.example.DTO.ClientStatusResponseDto;
import org.example.entity.Account;
import org.example.entity.Client;
import org.example.enums.AccountStatus;
import org.example.enums.TransactionStatus;
import org.example.repository.AccountRepository;
import org.example.repository.ClientRepository;
import org.example.repository.TransactionRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ClientStatusConsumer {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public ClientStatusConsumer(ClientRepository clientRepository,
                                AccountRepository accountRepository,
                                TransactionRepository transactionRepository) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @KafkaListener(topics = "t1_demo_client_status", groupId = "client-status-group")
    public void consume(String message) {
        try {
            ClientStatusResponseDto dto = parseJson(message);
            handleClientStatus(dto);
        } catch (Exception e) {
            System.err.println("Error processing client status message: " + message + ", error: " + e.getMessage());
        }
    }

    private void handleClientStatus(ClientStatusResponseDto dto) {
        String clientId = dto.getClientId();
        String statusStr = dto.getStatus();

        AccountStatus accountStatus = AccountStatus.valueOf(statusStr);

        // Обновляем статус клиента
        clientRepository.findByClientId(clientId).ifPresent(client -> {
            // TODO: можно добавить поле status у клиента
            // client.setStatus(accountStatus);
            // clientRepository.save(client);
        });

        // Обновляем все счета клиента
        accountRepository.findByClientId(clientId).forEach(account -> {
            if (AccountStatus.BLOCKED == accountStatus) {
                // Блокируем аккаунт
                account.setStatus(AccountStatus.BLOCKED);
                accountRepository.save(account);

                // Отклоняем все незавершённые транзакции
                transactionRepository.findByAccountIdAndStatus(account.getAccountId(), TransactionStatus.REQUESTED)
                        .forEach(transaction -> {
                            transaction.setStatus(TransactionStatus.REJECTED);
                            transactionRepository.save(transaction);
                        });
            }
        });
    }

    private ClientStatusResponseDto parseJson(String json) throws Exception {
        return new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, ClientStatusResponseDto.class);
    }
}