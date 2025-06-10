package org.example.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, String payload, String headerType) {
        Message<String> message = MessageBuilder.withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader("type", headerType)
                .build();
        try {
            kafkaTemplate.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message to Kafka", e);
        }
    }
}