package org.example.service3.controller;

import org.example.service3.service.UnlockService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/unlock")
public class UnlockController {

    private final UnlockService unlockService;

    public UnlockController(UnlockService unlockService) {
        this.unlockService = unlockService;
    }

    @PostMapping("/client")
    public String unlockClient(@RequestParam String clientId) {
        boolean success = unlockService.unlockClient(clientId);
        return success ? "Client unlocked" : "Client unlock denied";
    }

    @PostMapping("/account")
    public String unlockAccount(@RequestParam String accountId) {
        boolean success = unlockService.unlockAccount(accountId);
        return success ? "Account unlocked" : "Account unlock denied";
    }
}