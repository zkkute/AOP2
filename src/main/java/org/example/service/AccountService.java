package org.example.service;

import org.example.entity.Account;
import org.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private AccountRepository repository;

    public Account createAccount() {
        Account account = new Account();
        account.setStatus(Account.Status.OPEN);
        return repository.save(account);
    }
}