package org.example.service;
<<<<<<< HEAD
import org.example.annotations.Metric;
import org.springframework.stereotype.Service;

=======
import org.example.annotations.LogDatasourceError;
import org.example.annotations.Metric;
import org.springframework.stereotype.Service;


>>>>>>> cffc1cf01283d007f34d324665eef2bc27b5ae50
@Service
public class SomeService {

    @Metric
    public String heavyMethod() {
<<<<<<< HEAD
        // Имитация долгой операции
=======
>>>>>>> cffc1cf01283d007f34d324665eef2bc27b5ae50
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Result";
    }
<<<<<<< HEAD
=======

    @LogDatasourceError
    public String fetchData(String key) {
        if (key == null) {
            throw new RuntimeException("Key is null");
        }
        return "Data for " + key;
    }
>>>>>>> cffc1cf01283d007f34d324665eef2bc27b5ae50
}