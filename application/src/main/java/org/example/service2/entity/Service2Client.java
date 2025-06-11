package org.example.service2.entity;

import jakarta.persistence.*;
import org.example.enums.AccountStatus;

@Entity
@Table(name = "service2_client")
public class Service2Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", unique = true, nullable = false)
    private String clientId;

    @Column(nullable = false)
    private AccountStatus status;


    // Конструктор без аргументов (для JPA)
    public Service2Client() {}

    // Конструктор с clientId
    public Service2Client(String clientId) {
        this.clientId = clientId;
    }

    // Геттеры и сеттеры
    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public void setClientId(String clientId) {
        this.clientId = clientId;
    }



    // toString() метод

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", clientId='" + clientId + '\'' +
                '}';
    }
}