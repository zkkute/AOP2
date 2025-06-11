package org.example.service2.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.transaction")
@Getter
@Setter
public class TransactionLimitConfig {
    private int maxPerPeriod; // N
    private int periodSeconds; // T
}