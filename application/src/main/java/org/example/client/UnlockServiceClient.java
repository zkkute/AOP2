package org.example.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service3", url = "${app.service3.url}")
public interface UnlockServiceClient {

    @PostMapping("/api/unlock/client")
    String unlockClient(@RequestParam("clientId") String clientId);

    @PostMapping("/api/unlock/account")
    String unlockAccount(@RequestParam("accountId") String accountId);
}