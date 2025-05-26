package org.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;  // Импорт аннотации @Id
import jakarta.persistence.GeneratedValue;  // Импорт аннотации @GeneratedValue
import jakarta.persistence.GenerationType;  // Импорт перечисления GenerationType

import java.util.Date;

@Entity
public class TimeLimitExceedLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String methodName;
    private long executionTime;
    private Date timestamp;

    // Constructors
    public TimeLimitExceedLog() {}

    public TimeLimitExceedLog(String methodName, long executionTime, Date timestamp) {
        this.methodName = methodName;
        this.executionTime = executionTime;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }

    public long getExecutionTime() { return executionTime; }
    public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}