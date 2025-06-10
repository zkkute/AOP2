package org.example.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.DTO.IncomingTransactionDto;
import org.example.service.TransactionProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer {

    @Autowired
    private TransactionProcessingService transactionProcessingService;

    @KafkaListener(topics = "t1_demo_transactions", groupId = "transaction-group")
    public void consume(String message) {
        try {
            // Предполагается, что сообщение приходит в формате JSON
            IncomingTransactionDto dto = parseJson(message);
            transactionProcessingService.processIncomingTransaction(dto);
        } catch (Exception e) {
            System.err.println("Error processing message: " + message + ", error: " + e.getMessage());
        }
    }

    private IncomingTransactionDto parseJson(String json) {
        try {
            return new ObjectMapper().readValue(json, IncomingTransactionDto.class);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            System.err.println("JSON parsing error: " + e.getMessage());
            throw new IllegalArgumentException("Invalid JSON format: " + json, e);
        }
    }
}