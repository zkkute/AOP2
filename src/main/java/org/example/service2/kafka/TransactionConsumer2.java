package org.example.service2.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.service2.dto.TransactionAcceptDto;
import org.example.service2.service.TransactionProcessingService2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer2 {

    private final TransactionProcessingService2 processingService;

    public TransactionConsumer2(TransactionProcessingService2 processingService) {
        this.processingService = processingService;
    }

    @KafkaListener(topics = "t1_demo_transaction_accept", groupId = "transaction-result-group")
    public void consume(String message) {
        try {
            TransactionAcceptDto dto = parseJson(message);
            processingService.process(dto);
        } catch (Exception e) {
            System.err.println("Error processing message: " + message + ", error: " + e.getMessage());
        }
    }

    private TransactionAcceptDto parseJson(String json) throws Exception {
        return new ObjectMapper().readValue(json, TransactionAcceptDto.class);
    }
}