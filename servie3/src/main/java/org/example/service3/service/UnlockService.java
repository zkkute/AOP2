package org.example.service3.service;

import org.example.service3.enums.AccountStatus;
import org.example.service3.entity.Account;
import org.example.service3.entity.Client;
import org.example.service3.repository.AccountRepository;
import org.example.service3.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
public class UnlockService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final Random random = new Random();

    @Autowired
    public UnlockService(ClientRepository clientRepository, AccountRepository accountRepository) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public boolean unlockClient(String clientId) {
        return clientRepository.findByClientId(clientId).map(client -> {
            if (random.nextBoolean()) {
                client.setStatus(AccountStatus.OPEN);
                clientRepository.save(client);
                return true;
            } else {
                return false; // отказ в разблокировке
            }
        }).orElse(false);
    }

    @Transactional
    public boolean unlockAccount(String accountId) {
        return accountRepository.findByAccountId(accountId).map(account -> {
            if (random.nextBoolean()) {
                account.setStatus(AccountStatus.OPEN);
                accountRepository.save(account);
                return true;
            } else {
                return false; // отказ в снятии ареста
            }
        }).orElse(false);
    }
}