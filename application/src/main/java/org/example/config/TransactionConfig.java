package org.example.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.transaction")
@Getter
@Setter
public class TransactionConfig {
    private int maxRejectedBeforeArrested;
}