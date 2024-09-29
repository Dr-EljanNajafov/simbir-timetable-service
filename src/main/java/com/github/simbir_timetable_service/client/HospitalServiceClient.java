package com.github.simbir_timetable_service.client;

import com.github.simbir_timetable_service.dto.HospitalDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "hospital-service", url = "${services.hospital-service.host}:${services.hospital-service.port}")
@Component
public interface HospitalServiceClient {
    @GetMapping("/api/Hospitals/{id}")
    ResponseEntity<HospitalDto> getHospitalById(@PathVariable("id") Long hospitalId, @RequestHeader("Authorization") String bearerToken);
}
