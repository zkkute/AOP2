package org.example.service;

import org.springframework.stereotype.Service;

@Service
public class SomeService {

    public String heavyMethod() {
        // Имитация долгой операции
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Result";
    }

    public String fetchData(String key) {
        if (key == null) {
            throw new RuntimeException("Key is null");
        }
        return "Data for " + key;
    }
}