package org.example.service;

import org.example.annotations.Cached;
import org.springframework.stereotype.Repository;

@Repository
public class DataRepository {

    @Cached
    public String getData(String key) {
        // Имитация запроса к БД
        System.out.println("Fetching data from DB for key: " + key);
        return "Data for " + key;
    }
}