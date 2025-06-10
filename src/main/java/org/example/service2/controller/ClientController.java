package org.example.service2.controller;

import org.example.enums.AccountStatus;
import org.example.service2.entity.Client;
import org.example.service2.repository.ClientRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client")
public class ClientController {
    private final ClientRepository clientRepository;

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping("/status")
    public String getClientStatus(@RequestParam String clientId,
                                  @RequestParam String accountId) {
        return clientRepository.findByClientId(clientId)
                .map(client -> client.getStatus().name()) // явный вызов
                .orElse(AccountStatus.CLOSED.name());
    }
}