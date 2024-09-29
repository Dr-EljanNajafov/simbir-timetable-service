package com.github.simbir_timetable_service.client;

import com.github.simbir_timetable_service.dto.AccountDto;
import com.github.simbir_timetable_service.dto.DoctorDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "account-service", url = "${services.account-service.host}:${services.account-service.port}")
@Component
public interface AccountServiceClient {

    @GetMapping("/api/Authentication/Validate")
    ResponseEntity<String> validateToken(@RequestParam("accessToken") String token);

    @GetMapping("/api/Authentication/Me")
    ResponseEntity<AccountDto> getCurrentAccount(@RequestHeader("Authorization") String bearerToken);

    @GetMapping("/api/Accounts/{id}")
    ResponseEntity<AccountDto> getAccountById(@PathVariable("id") Long id, @RequestHeader("Authorization") String bearerToken);

    @GetMapping("/api/Doctors/{id}")
    ResponseEntity<DoctorDto> getDoctorById(@PathVariable("id") Long id, @RequestHeader("Authorization") String bearerToken);
}
