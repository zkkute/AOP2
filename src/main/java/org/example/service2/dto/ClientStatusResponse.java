package org.example.service2.dto;

public class ClientStatusResponse {
    private String clientId;
    private String status;

    public ClientStatusResponse(String clientId, String status) {
        this.clientId = clientId;
        this.status = status;
    }

    // Геттеры и сеттеры
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}