package org.example.aspect;
<<<<<<< HEAD

import org.example.entity.TimeLimitExceedLog; // Импорт сущности
import org.example.repository.TimeLimitExceedLogRepository; // Импорт репозитория
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import org.example.kafka.KafkaProducerService;
=======
import org.apache.kafka.clients.producer.ProducerRecord;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.entity.TimeLimitExceedLog;
import org.example.repository.TimeLimitExceedLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
>>>>>>> cffc1cf01283d007f34d324665eef2bc27b5ae50

@Aspect
@Component
public class MetricAspect {

    @Value("${app.metrics.time-limit}")
    private long timeLimit;

<<<<<<< HEAD
    @Autowired
    private KafkaProducerService kafkaProducerService;
=======
    @Value("${app.metrics.kafka-topic}")
    private String kafkaTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
>>>>>>> cffc1cf01283d007f34d324665eef2bc27b5ae50

    @Autowired
    private TimeLimitExceedLogRepository repository;

<<<<<<< HEAD
    @Around("@annotation(org.example.annotations.Metric)")
=======
    @Around("@annotation(Metric)")
>>>>>>> cffc1cf01283d007f34d324665eef2bc27b5ae50
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;

        if (duration > timeLimit) {
            String methodName = joinPoint.getSignature().getName();
<<<<<<< HEAD

            // Подготовка данных
            String message = String.format("Method '%s' exceeded time limit: %d ms", methodName, duration);
            String timestampStr = new Date().toString();

            try {
                // Попытка отправить в Kafka
                kafkaProducerService.sendMessage("t1_demo_metrics", message, "METRICS");
            } catch (Exception ex) {
                // Логируем и пишем в БД как fallback
                System.err.println("Failed to send to Kafka: " + ex.getMessage());
                TimeLimitExceedLog logEntry = new TimeLimitExceedLog(methodName, duration, new Date());
=======
            String message = "Method " + methodName + " exceeded time limit: " + duration + " ms";

            try {

                kafkaTemplate.execute(operations -> {
                    ProducerRecord<String, String> record = new ProducerRecord<>(kafkaTopic, message);
                    operations.send(record);
                    return null;
                });
            } catch (Exception e) {
                TimeLimitExceedLog logEntry = new TimeLimitExceedLog();
                logEntry.setMethodName(methodName);
                logEntry.setExecutionTime(duration);
                logEntry.setTimestamp(new Date());
>>>>>>> cffc1cf01283d007f34d324665eef2bc27b5ae50
                repository.save(logEntry);
            }
        }

        return result;
    }
}