package org.example.service2.controller;

import org.example.service2.dto.ClientStatusResponse;
import org.example.service2.entity.Client;
import org.example.service2.repository.ClientRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/client")
public class ClientStatusController {

    private final ClientRepository clientRepository;
    private final Random random = new Random();

    public ClientStatusController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping("/status")
    public ClientStatusResponse getClientStatus(
            @RequestParam("clientId") String clientId,
            @RequestParam("accountId") String accountId) {

        // Проверяем, есть ли клиент в базе данных
        Optional<Client> client = clientRepository.findByClientId(clientId);
        if (client.isEmpty()) {
            // Можно создать нового клиента с дефолтным статусом
            client = Optional.of(new Client());
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