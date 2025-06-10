package org.example.aspect;

import org.example.entity.TimeLimitExceedLog;
import org.example.repository.TimeLimitExceedLogRepository;
import org.example.kafka.KafkaProducerService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
public class MetricAspect {

    @Value("${app.metrics.time-limit}")
    private long timeLimit;

    @Value("${app.metrics.kafka-topic}")
    private String kafkaTopic;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private TimeLimitExceedLogRepository repository;

    @Around("@annotation(org.example.annotations.Metric)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;

        if (duration > timeLimit) {
            String methodName = joinPoint.getSignature().getName();
            String message = String.format("Method '%s' exceeded time limit: %d ms", methodName, duration);

            try {
                // Попытка отправить в Kafka через KafkaProducerService
                kafkaProducerService.sendMessage(kafkaTopic, message, "METRICS");
            } catch (Exception ex) {
                // Если Kafka недоступна или произошла ошибка — резервная запись в БД
                System.err.println("Failed to send to Kafka: " + ex.getMessage());
                TimeLimitExceedLog logEntry = new TimeLimitExceedLog(methodName, duration, new Date());
                repository.save(logEntry);
            }
        }

        return result;
    }
}