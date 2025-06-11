package org.example.starter.aspect;

import org.example.starter.entity.TimeLimitExceedLog;
import org.example.starter.service.kafka.KafkaProducerService;
import org.example.starter.service.repository.DataSourceErrorLogRepository;
import org.example.starter.service.repository.TimeLimitExceedLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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
                kafkaProducerService.sendMessage(kafkaTopic, message, "METRICS");
            } catch (Exception ex) {
                repository.save(new TimeLimitExceedLog(methodName, duration, new Date()));
            }
        }

        return result;
    }
}