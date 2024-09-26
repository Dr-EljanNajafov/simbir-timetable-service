package com.github.simbir_timetable_service.dto;

public record AccountDto(
        Long id,
        String username,
        String lastName,
        String firstName,
        Role role
) {
}