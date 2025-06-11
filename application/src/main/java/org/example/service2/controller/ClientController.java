package org.example.service2.controller;

import org.example.enums.AccountStatus;
import org.example.repository.ClientRepository;
import org.example.service2.entity.Service2Client;
import org.example.service2.repository.Service2ClientRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client")
public class ClientController {
    private final Service2ClientRepository clientRepository;

    public ClientController(Service2ClientRepository clientRepository) {
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