package org.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
@SpringBootApplication
@EnableConfigurationProperties
@ComponentScan(basePackages = {
        "org.example.starter.config",
        "org.example.starter.service.repository",
        "org.example.starter.entity"
})
public class MetricsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MetricsApplication.class, args);
    }
}