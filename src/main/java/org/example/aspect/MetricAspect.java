package org.example.aspect;

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

@Aspect
@Component
public class MetricAspect {

    @Value("${app.metrics.time-limit}")
    private long timeLimit;

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
                repository.save(logEntry);
            }
        }

        return result;
    }
}