package com.github.simbir_timetable_service.dto.request;

import lombok.Data;

@Data
public class AppointmentRequest {
    private String time; // Время в формате ISO8601
}