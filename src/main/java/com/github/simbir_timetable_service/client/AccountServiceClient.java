package com.github.simbir_timetable_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

// Пример Feign-клиента для AccountService
@FeignClient(name = "account-service", url = "${services.account-service.host}:${services.account-service.port}")
@Component
public interface AccountServiceClient {

    @GetMapping("/api/Authentication/Validate")
    ResponseEntity<String> validateToken(@RequestParam("accessToken") String token);
    
}