package org.example.service2.controller;

import org.example.repository.ClientRepository;
import org.example.service2.dto.ClientStatusResponse;
import org.example.service2.entity.Service2Client;
import org.example.service2.repository.Service2ClientRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/client")
public class ClientStatusController {

    private final Service2ClientRepository clientRepository;
    private final Random random = new Random();

    public ClientStatusController(Service2ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping("/status")
    public ClientStatusResponse getClientStatus(
            @RequestParam("clientId") String clientId,
            @RequestParam("accountId") String accountId) {

        // Проверяем, есть ли клиент в базе данных
        Optional<Service2Client> client = clientRepository.findByClientId(clientId);
        if (client.isEmpty()) {
            // Можно создать нового клиента с дефолтным статусом
            client = Optional.of(new Service2Client());
            client.get().setClientId(clientId);
        }

        // Пример: 10% шанс попадания в BLOCKED
        boolean isBlacklisted = random.nextDouble() < 0.10; // 10% вероятность
        String status;

        if (isBlacklisted) {
            status = "BLOCKED";
        } else {
            status = "OPEN"; // Или можно проверить поле в БД
        }

        return new ClientStatusResponse(clientId, status);
    }
}