package org.example.transactionaccept.config;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.transactionaccept.dto.TransactionAcceptRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration("transactionKafkaConfig")
@EnableKafka
public class KafkaProConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionAcceptRequest> transactionAcceptFactory(
            ConsumerFactory<String, TransactionAcceptRequest> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, TransactionAcceptRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, TransactionAcceptRequest> transactionAcceptConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "transaction-accept-group");
        props.put("key.deserializer", StringDeserializer.class);
        props.put("value.deserializer", JsonDeserializer.class);
        props.put("json.value.default.typ", TransactionAcceptRequest.class.getName());
        return new DefaultKafkaConsumerFactory<>(props);
    }
}