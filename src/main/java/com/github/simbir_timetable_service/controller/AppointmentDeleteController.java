package com.github.simbir_timetable_service.controller;

import com.github.simbir_timetable_service.client.AccountServiceClient;
import com.github.simbir_timetable_service.client.HospitalServiceClient;
import com.github.simbir_timetable_service.config.context.UserContext;
import com.github.simbir_timetable_service.dto.AccountDto;
import com.github.simbir_timetable_service.repository.TimetableRepository;
import com.github.simbir_timetable_service.service.TimetableService;
import com.github.simbir_timetable_service.timetable.Timetable;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/Appointment")
@RequiredArgsConstructor
public class AppointmentDeleteController {

    private final TimetableService timetableService;
    private final AccountServiceClient accountServiceClient;
    private final TimetableRepository timetableRepository;
    private final UserContext userContext;

    private boolean isUserAuthorized(String role) {
        List<String> roles = userContext.getRoles();
        return roles.contains(role);
    }

    @Operation(summary = "Отменить запись на приём")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id, HttpServletRequest request) {

        // Извлекаем токен из заголовка запроса
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Токен не найден или недействителен.");
        }

        // Получаем данные аккаунта через Feign-клиент
        AccountDto account = accountServiceClient.getCurrentAccount(bearerToken).getBody();

        // Находим запись в расписании по id
        Timetable timetable = timetableRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись не найдена"));

        // Получаем идентификатор пациента, связанного с записью в расписании
        Long patientId = timetable.getPatientId();

        // Проверяем права: только администратор, менеджер или сам пользователь могут удалить запись
        assert account != null;
        Long userId = account.id(); // получаем ID пользователя из аккаунта

        if (!isUserAuthorized("admin") && !isUserAuthorized("manager") && !Objects.equals(userId, patientId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ запрещен: недостаточно прав.");
        }

        // Удаляем запись
        timetableService.deleteTimetable(id);
        return ResponseEntity.noContent().build(); // Возвращаем статус 204 No Content
    }
}

