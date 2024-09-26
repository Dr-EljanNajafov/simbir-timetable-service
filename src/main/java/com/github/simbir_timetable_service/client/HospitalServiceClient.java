package com.github.simbir_timetable_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Пример Feign-клиента для HospitalService
@FeignClient(name = "hospital-service", url = "${services.hospital-service.host}:${services.hospital-service.port}")
@Component
public interface HospitalServiceClient {
    @GetMapping("/api/Hospitals/{id}")
    void getHospitalById(@PathVariable("id") Integer hospitalId);
}
