package com.github.simbir_timetable_service.controller;

import com.github.simbir_timetable_service.client.AccountServiceClient;
import com.github.simbir_timetable_service.client.HospitalServiceClient;
import com.github.simbir_timetable_service.config.context.UserContext;
import com.github.simbir_timetable_service.service.TimetableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/Appointment")
@RequiredArgsConstructor
public class AppointmentDeleteController {  // todo проблемы с логикой, добавить в таблицу новый столбец userID

    private final TimetableService timetableService;
    private final AccountServiceClient accountServiceClient;
    private final HospitalServiceClient hospitalServiceClient;
    private final UserContext userContext;

    private boolean isUserAuthorized(String role) {
        List<String> roles = userContext.getRoles();
        return roles.contains(role);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id) {
        if (!isUserAuthorized("admin") && !isUserAuthorized("manager")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ запрещен: недостаточно прав.");
        }


        timetableService.deleteTimetable(id);
        return ResponseEntity.noContent().build(); // Возвращаем статус 204 No Content
    }
}
