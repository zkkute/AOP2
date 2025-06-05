package org.example.aspect;

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

@Aspect
@Component
public class MetricAspect {

    @Value("${app.metrics.time-limit}")
    private long timeLimit;

    @Value("${app.metrics.kafka-topic}")
    private String kafkaTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private TimeLimitExceedLogRepository repository;

    @Around("@annotation(org.example.annotations.Metric)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;

        if (duration > timeLimit) {
            String methodName = joinPoint.getSignature().getName();
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
                repository.save(logEntry);
            }
        }

        return result;
    }
}