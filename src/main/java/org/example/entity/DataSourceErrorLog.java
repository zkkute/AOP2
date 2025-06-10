package org.example.entity;

<<<<<<< HEAD
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
=======
import jakarta.persistence.*;


>>>>>>> cffc1cf01283d007f34d324665eef2bc27b5ae50
import java.util.Date;

@Entity
public class DataSourceErrorLog {
<<<<<<< HEAD
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
=======
>>>>>>> cffc1cf01283d007f34d324665eef2bc27b5ae50
    private Long id;
    private String methodName;
    private String errorMessage;
    private Date timestamp;

<<<<<<< HEAD
    public DataSourceErrorLog() {}

    public DataSourceErrorLog(String methodName, String errorMessage, Date timestamp) {
        this.methodName = methodName;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
=======
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
>>>>>>> cffc1cf01283d007f34d324665eef2bc27b5ae50
}