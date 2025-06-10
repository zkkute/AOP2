package org.example.service;

import org.example.annotations.LogDatasourceError;
import org.springframework.stereotype.Service;

@Service
public class DataService {

    @LogDatasourceError
    public String fetchDataFromDataSource(String key) {
        // Имитация ошибки
        if ("error".equals(key)) {
            throw new RuntimeException("Database connection failed");
        }
        return "Data for " + key;
    }
}