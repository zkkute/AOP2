package org.example.service;

import org.example.enums.AccountStatus;
import org.example.metrics.AccountMetrics;
import org.example.repository.AccountRepository;
import org.example.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
public class UnlockService {

    private final AccountMetrics accountMetrics;
    private ClientRepository clientRepository;
    private AccountRepository accountRepository;
    private Random random;

    public UnlockService(AccountMetrics accountMetrics) {
        this.accountMetrics = accountMetrics;
    }

    @Transactional
    public boolean unlockClient(String clientId) {
        return clientRepository.findByClientId(clientId).map(client -> {
            if (random.nextBoolean()) {
                client.setStatus(AccountStatus.OPEN);
                accountMetrics.incrementBlockedClients(); // Увеличиваем метрику
                return true;
            } else {
                return false;
            }
        }).orElse(false);
    }

    @Transactional
    public boolean unlockAccount(String accountId) {
        return accountRepository.findByAccountId(accountId).map(account -> {
            if (random.nextBoolean()) {
                account.setStatus(AccountStatus.OPEN);
                accountMetrics.incrementArrestedAccounts(); // Увеличиваем метрику
                return true;
            } else {
                return false;
            }
        }).orElse(false);
    }
}