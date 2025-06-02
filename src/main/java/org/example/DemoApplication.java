package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableKafka // Включает поддержку Kafka
@EnableAspectJAutoProxy // Явно включает AOP прокси
@EnableJpaRepositories("org.example.repository") // Указывает пакет для репозиториев
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}