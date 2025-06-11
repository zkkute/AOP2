package org.example.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service2", url = "${app.service2.url}") // настройка URL в application.properties
public interface ClientStatusClient {
    @GetMapping("/api/client/status")
    String getClientStatus(@RequestParam("clientId") String clientId,
                           @RequestParam("accountId") String accountId);
}