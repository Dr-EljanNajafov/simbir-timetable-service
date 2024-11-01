package com.github.simbir_timetable_service.controller;

import com.github.simbir_timetable_service.client.AccountServiceClient;
import com.github.simbir_timetable_service.client.HospitalServiceClient;
import com.github.simbir_timetable_service.config.context.UserContext;
import com.github.simbir_timetable_service.dto.DoctorDto;
import com.github.simbir_timetable_service.dto.HospitalDto;
import com.github.simbir_timetable_service.dto.TimetableDto;
import com.github.simbir_timetable_service.dto.request.AppointmentRequest;
import com.github.simbir_timetable_service.service.TimetableService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/Timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;
    private final AccountServiceClient accountServiceClient;
    private final HospitalServiceClient hospitalServiceClient;
    private final UserContext userContext;

    private boolean isUserAuthorized(String role) {
        List<String> roles = userContext.getRoles();
        return roles.contains(role);
    }


    @Operation(summary = "Создание новой записи в расписании")
    @PostMapping
    public ResponseEntity<TimetableDto> createTimetable(@RequestBody TimetableDto timetableDto) {

        // Проверка прав доступа для администраторов и менеджеров
        if (!isUserAuthorized("admin") && !isUserAuthorized("manager")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ запрещен: недостаточно прав.");
        }

        // Сначала проверяем наличие больницы
        HospitalDto hospitalDto = hospitalServiceClient.getHospitalById(timetableDto.hospitalId(), userContext.getToken()).getBody();
        if (hospitalDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Больница не найдена.");
        }

        if(!hospitalDto.rooms().contains(timetableDto.room())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Комната не найдена.");
        }

        // Проверка доктора
        DoctorDto doctorDto = accountServiceClient.getDoctorById(timetableDto.doctorId(), userContext.getToken()).getBody();
        if (doctorDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Доктор не найден.");
        }

        TimetableDto createdTimetable = timetableService.createTimetable(timetableDto, userContext.getToken());
        return ResponseEntity.status(201).body(createdTimetable);
    }

    @Operation(summary = "Обновление записи расписания")
    @PutMapping("/{id}")
    public ResponseEntity<TimetableDto> updateTimetable(
            @PathVariable Long id,
            @RequestBody TimetableDto timetableDto
    ) {
        if (!isUserAuthorized("admin") && !isUserAuthorized("manager")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ запрещен: недостаточно прав.");
        }

        // Сначала проверяем наличие больницы
        HospitalDto hospitalDto = hospitalServiceClient.getHospitalById(timetableDto.hospitalId(), userContext.getToken()).getBody();
        if (hospitalDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Больница не найдена.");
        }

        if(!hospitalDto.rooms().contains(timetableDto.room())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Комната не найдена.");
        }

        // Проверка доктора
        DoctorDto doctorDto = accountServiceClient.getDoctorById(timetableDto.doctorId(), userContext.getToken()).getBody();
        if (doctorDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Доктор не найден.");
        }

        TimetableDto updatedDocument = timetableService.updateTimetable(id, timetableDto);
        return ResponseEntity.ok(updatedDocument);
    }

    @Operation(summary = "Удаление записи расписания")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimetable(@PathVariable Long id) {
        if (!isUserAuthorized("admin") && !isUserAuthorized("manager")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ запрещен: недостаточно прав.");
        }

        timetableService.deleteTimetable(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удаление записей расписания доктора")
    @DeleteMapping("/Doctor/{id}")
    public ResponseEntity<Void> deleteTimetableByDoctor(@PathVariable Long id) {
        if (!isUserAuthorized("admin") && !isUserAuthorized("manager")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ запрещен: недостаточно прав.");
        }

        timetableService.deleteTimetableByDoctor(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Возвращаем статус 204 No Content
    }

    @Operation(summary = "Удаление записей расписания больницы")
    @DeleteMapping("/Hospital/{id}")
    public ResponseEntity<Void> deleteTimetableByHospital(@PathVariable Long id) {
        if (!isUserAuthorized("admin") && !isUserAuthorized("manager")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ запрещен: недостаточно прав.");
        }

        timetableService.deleteTimetableByHospital(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Возвращаем статус 204 No Content
    }

    @Operation(summary = "Получение расписания больницы по Id")
    @GetMapping("/Hospital/{id}")
    public ResponseEntity<List<TimetableDto>> getHospitalTimetable(
            @PathVariable Long id,
            @RequestParam String from,
            @RequestParam String to) {
        LocalDateTime fromDateTime = LocalDateTime.parse(from);
        LocalDateTime toDateTime = LocalDateTime.parse(to);

        List<TimetableDto> timetable = timetableService.getTimetableByHospital(id, fromDateTime, toDateTime);
        return ResponseEntity.ok(timetable);
    }

    @Operation(summary = "Получение расписания врача по Id")
    @GetMapping("/Doctor/{id}")
    public ResponseEntity<List<TimetableDto>> getDoctorTimetable(
            @PathVariable Long id,
            @RequestParam String from,
            @RequestParam String to) {
        LocalDateTime fromDateTime = LocalDateTime.parse(from);
        LocalDateTime toDateTime = LocalDateTime.parse(to);

        List<TimetableDto> timetable = timetableService.getTimetableByDoctor(id, fromDateTime, toDateTime);
        return ResponseEntity.ok(timetable);
    }

    @Operation(summary = "Получение расписания кабинета больницы")
    @GetMapping("/Hospital/{id}/Room/{room}")
    public ResponseEntity<List<TimetableDto>> getRoomTimetable(
            @PathVariable Long id,
            @PathVariable String room,
            @RequestParam String from,
            @RequestParam String to) {
        LocalDateTime fromDateTime = LocalDateTime.parse(from);
        LocalDateTime toDateTime = LocalDateTime.parse(to);

        if (!isUserAuthorized("admin") && !isUserAuthorized("manager") && !isUserAuthorized("doctor")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ запрещен: недостаточно прав.");
        }

        List<TimetableDto> timetable = timetableService.getTimetableByRoom(id, room, fromDateTime, toDateTime);
        return ResponseEntity.ok(timetable);
    }

    @Operation(summary = "Получение свободных талонов на приём")
    @GetMapping("/{id}/Appointments")
    public ResponseEntity<List<LocalDateTime>> getAvailableAppointments(@PathVariable Long id) {
        List<LocalDateTime> availableAppointments = timetableService.getAvailableAppointments(id);
        return ResponseEntity.ok(availableAppointments);
    }

    @Operation(summary = "Записаться на приём")
    @PostMapping("/{id}/Appointments")
    public ResponseEntity<TimetableDto> bookAppointment(@PathVariable Long id, @RequestBody AppointmentRequest appointmentRequest) {
        TimetableDto bookedTimetable = timetableService.bookAppointment(id, appointmentRequest.getTime());
        return ResponseEntity.status(HttpStatus.CREATED).body(bookedTimetable);
    }
}
