package com.github.simbir_timetable_service.dto;

import java.time.LocalDateTime;

public record TimetableDto (
    Long id,
    Long hospitalId,
    Long doctorId,
    LocalDateTime from,
    LocalDateTime to,
    String room
) {
}
