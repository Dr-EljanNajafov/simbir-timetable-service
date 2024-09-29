package com.github.simbir_timetable_service.dto;

import java.util.List;

public record HospitalDto(
        String name,
        String address,
        String contactPhone,
        List<String> rooms
) {
}