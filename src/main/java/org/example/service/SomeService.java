package org.example.service;

import org.example.annotations.Metric;
import org.springframework.stereotype.Service;

import org.example.annotations.LogDatasourceError;
import org.example.annotations.Metric;
import org.springframework.stereotype.Service;

@Service
public class SomeService {

    @Metric
    public String heavyMethod() {
        // Имитация долгой операции
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Result";
    }

    @LogDatasourceError
    public String fetchData(String key) {
        if (key == null) {
            throw new RuntimeException("Key is null");
        }
        return "Data for " + key;
    }
}