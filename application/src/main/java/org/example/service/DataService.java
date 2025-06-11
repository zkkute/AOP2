package org.example.service;

import org.springframework.stereotype.Service;

@Service
public class DataService {

    public String fetchDataFromDataSource(String key) {
        // Имитация ошибки
        if ("error".equals(key)) {
            throw new RuntimeException("Database connection failed");
        }
        return "Data for " + key;
    }
}