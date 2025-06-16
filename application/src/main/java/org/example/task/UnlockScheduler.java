package org.example.task;

import org.example.client.UnlockServiceClient;
import org.example.entity.Client;
import org.example.entity.Account;
import org.example.repository.ClientRepository;
import org.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UnlockScheduler {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final UnlockServiceClient unlockServiceClient;

    @Value("${app.unlock-task.client-batch-size}")
    private int clientBatchSize;

    @Value("${app.unlock-task.account-batch-size}")
    private int accountBatchSize;

    public UnlockScheduler(ClientRepository clientRepository,
                           AccountRepository accountRepository,
                           UnlockServiceClient unlockServiceClient) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
        this.unlockServiceClient = unlockServiceClient;
    }

    // Каждые N миллисекунд отправляем N клиентов на разблокировку
    @Scheduled(fixedRateString = "${app.unlock-task.interval-ms-client}")
    public void scheduleClientUnlock() {
        List<Client> clients = clientRepository.findTopNBlocked(clientBatchSize);
        for (Client client : clients) {
            try {
                String response = unlockServiceClient.unlockClient(client.getClientId());
                System.out.println("Unlocked client " + client.getClientId() + ": " + response);
            } catch (Exception e) {
                System.err.println("Failed to unlock client " + client.getClientId() + ": " + e.getMessage());
            }
        }
    }

    // Каждые M миллисекунд отправляем M счетов на снятие ареста
    @Scheduled(fixedRateString = "${app.unlock-task.interval-ms-account}")
    public void scheduleAccountUnlock() {
        List<Account> accounts = accountRepository.findTopNArrested(accountBatchSize);
        for (Account account : accounts) {
            try {
                String response = unlockServiceClient.unlockAccount(account.getAccountId());
                System.out.println("Unlocked account " + account.getAccountId() + ": " + response);
            } catch (Exception e) {
                System.err.println("Failed to unlock account " + account.getAccountId() + ": " + e.getMessage());
            }
        }
    }
}