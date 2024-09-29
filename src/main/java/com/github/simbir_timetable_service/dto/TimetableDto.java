package com.github.simbir_timetable_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record TimetableDto(
        Long hospitalId,
        Long doctorId,
        LocalDateTime from,
        LocalDateTime to,
        String room,
        @Schema(hidden = true) Long patientId
) {
}
