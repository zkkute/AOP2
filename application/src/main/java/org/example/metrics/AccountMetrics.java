package org.example.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class AccountMetrics {

    private final Counter blockedClientsCounter;
    private final Counter arrestedAccountsCounter;

    public AccountMetrics(MeterRegistry registry) {
        this.blockedClientsCounter = registry.counter("clients.blocked");
        this.arrestedAccountsCounter = registry.counter("accounts.arrested");
    }

    public void incrementBlockedClients() {
        blockedClientsCounter.increment();
    }

    public void incrementArrestedAccounts() {
        arrestedAccountsCounter.increment();
    }
}