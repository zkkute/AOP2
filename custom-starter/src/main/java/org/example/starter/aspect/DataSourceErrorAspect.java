package org.example.starter.aspect;

import org.example.starter.annotation.LogDatasourceError;
import org.example.starter.entity.DataSourceErrorLog;
import org.example.starter.service.kafka.KafkaProducerService;
import org.example.starter.service.repository.DataSourceErrorLogRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Aspect
@Component
public class DataSourceErrorAspect {

    @Value("${app.datasource.kafka-topic}")
    private String kafkaTopic;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private DataSourceErrorLogRepository dataSourceErrorLogRepository;

    @Around("@annotation(org.example.annotations.LogDatasourceError)")
    public Object logDataSourceErrors(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception ex) {
            String methodName = joinPoint.getSignature().getName();
            String errorMessage = ex.getMessage();
            String message = String.format("Data source error in method '%s': %s", methodName, errorMessage);
            Date timestamp = new Date();

            try {
                // Отправляем в Kafka
                kafkaProducerService.sendMessage(kafkaTopic, message, "DATA_SOURCE");
            } catch (Exception kafkaEx) {
                // Логируем и пишем в БД как fallback
                System.err.println("Failed to send to Kafka: " + kafkaEx.getMessage());
                DataSourceErrorLog logEntry = new DataSourceErrorLog(methodName, errorMessage, timestamp);
                dataSourceErrorLogRepository.save(logEntry);
            }

            throw ex; // повторно выбрасываем исходное исключение
        }
    }
}