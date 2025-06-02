package org.example.aspect;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.entity.DataSourceErrorLog;
import org.example.repository.DataSourceErrorLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
public class LogDatasourceErrorAspect {

    @Value("${app.datasource.kafka-topic}")
    private String kafkaTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private DataSourceErrorLogRepository repository;

    @Around("@annotation(LogDatasourceError)")
    public Object logDataSourceError(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            String methodName = joinPoint.getSignature().getName();
            String errorMessage = e.getMessage();
            String message = "Data source error in method " + methodName + ": " + errorMessage;

            try {
                // Отправка в Kafka
                ProducerRecord<String, String> record = new ProducerRecord<>(kafkaTopic, message);
                kafkaTemplate.send(record);
            } catch (Exception kafkaError) {
                // Логирование в БД
                DataSourceErrorLog logEntry = new DataSourceErrorLog();
                logEntry.setMethodName(methodName);
                logEntry.setErrorMessage(errorMessage);
                logEntry.setTimestamp(new Date());
                repository.save(logEntry);
            }

            throw e; // Перебросить исключение
        }
    }
}