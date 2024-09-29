package com.github.simbir_timetable_service.service;

import com.github.simbir_timetable_service.client.AccountServiceClient;
import com.github.simbir_timetable_service.config.context.UserContext;
import com.github.simbir_timetable_service.dto.TimetableDto;
import com.github.simbir_timetable_service.dto.mapper.TimetableDtoMapper;
import com.github.simbir_timetable_service.repository.TimetableRepository;
import com.github.simbir_timetable_service.timetable.Timetable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TimetableDtoMapper timetableDtoMapper;
    private final TimetableRepository timetableRepository;
    private final AccountServiceClient accountServiceClient;
    private final UserContext userContext;
    public TimetableDto createTimetable (TimetableDto timetableDto, String bearerToken) {
        Timetable timetable = new Timetable();
        timetable.setHospitalId(timetableDto.hospitalId());
        timetable.setDoctorId(timetableDto.doctorId());
        timetable.setFrom(timetableDto.from());
        timetable.setTo(timetableDto.to());
        timetable.setRoom(timetableDto.room());
        timetable.setPatientId(Objects.requireNonNull(accountServiceClient.getCurrentAccount(bearerToken).getBody()).id());
        Timetable savedTimetable = timetableRepository.save(timetable);
        return timetableDtoMapper.apply(savedTimetable);
    }

    public TimetableDto updateTimetable(Long id, TimetableDto timetableDto) {
        Timetable timetable = timetableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Timetable not found"));
        timetable.setHospitalId(timetableDto.hospitalId());
        timetable.setDoctorId(timetableDto.doctorId());
        timetable.setFrom(timetableDto.from());
        timetable.setTo(timetableDto.to());
        timetable.setRoom(timetableDto.room());
        Timetable updatedTimetable = timetableRepository.save(timetable);
        return timetableDtoMapper.apply(updatedTimetable);
    }


    public void deleteTimetable(Long id) {
        Timetable timetable = timetableRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Timetable not found"));
        timetableRepository.delete(timetable);
    }

    @Transactional
    public void deleteTimetableByDoctor(Long doctorId) {
        // Логика удаления расписания по doctorId
        timetableRepository.deleteTimetableByDoctorId(doctorId);
    }

    @Transactional
    public void deleteTimetableByHospital(Long hospitalId) {
        // Логика удаления расписания по hospitalId
        timetableRepository.deleteTimetableByHospitalId(hospitalId);
    }

    public List<TimetableDto> getTimetableByHospital(Long hospitalId, LocalDateTime from, LocalDateTime to) {
        List<Timetable> timetables = timetableRepository.findByHospitalIdAndFromBetween(hospitalId, from, to);
        return timetables.stream().map(timetableDtoMapper::apply).collect(Collectors.toList());
    }

    public List<TimetableDto> getTimetableByDoctor(Long doctorId, LocalDateTime from, LocalDateTime to) {
        List<Timetable> timetables = timetableRepository.findByDoctorIdAndFromBetween(doctorId, from, to);
        return timetables.stream().map(timetableDtoMapper::apply).collect(Collectors.toList());
    }

    // Новый метод для получения расписания по кабинету
    public List<TimetableDto> getTimetableByRoom(Long hospitalId, String room, LocalDateTime from, LocalDateTime to) {
        List<Timetable> timetables = timetableRepository.findByHospitalIdAndRoomAndFromBetween(hospitalId, room, from, to);
        return timetables.stream().map(timetableDtoMapper::apply).collect(Collectors.toList());
    }

    public List<LocalDateTime> getAvailableAppointments(Long timetableId) {
        Timetable timetable = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Timetable not found"));

        List<LocalDateTime> availableAppointments = new ArrayList<>();
        LocalDateTime start = timetable.getFrom();
        LocalDateTime end = timetable.getTo();

        // Интервал 30 минут
        Duration appointmentInterval = Duration.ofMinutes(30);

        // Генерация доступных талонов
        for (LocalDateTime time = start; time.isBefore(end); time = time.plus(appointmentInterval)) {
            availableAppointments.add(time);
        }

        return availableAppointments;
    }

    public TimetableDto bookAppointment(Long timetableId, String time) {
        Timetable timetable = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Timetable not found"));

        LocalDateTime appointmentTime = LocalDateTime.parse(time);

        // Проверка, доступно ли время
        if (appointmentTime.isBefore(timetable.getFrom()) || appointmentTime.isAfter(timetable.getTo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Время не входит в расписание.");
        }

        // Проверка, забронировано ли уже это время
        boolean isTimeBooked = timetableRepository.existsByDoctorIdAndRoomAndFromAndTo(
                timetable.getDoctorId(),
                timetable.getRoom(),
                appointmentTime,
                appointmentTime.plusMinutes(30)
        );

        if (isTimeBooked) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Это время уже забронировано.");
        }

        // Логика для добавления записи
        Timetable newAppointment = new Timetable();
        newAppointment.setHospitalId(timetable.getHospitalId());
        newAppointment.setDoctorId(timetable.getDoctorId());
        newAppointment.setFrom(appointmentTime);
        newAppointment.setTo(appointmentTime.plusMinutes(30)); // Предположим, что приём длится 30 минут
        newAppointment.setRoom(timetable.getRoom());
        newAppointment.setPatientId(Objects.requireNonNull(accountServiceClient.getCurrentAccount(userContext.getToken()).getBody()).id());

        Timetable savedTimetable = timetableRepository.save(newAppointment);
        return timetableDtoMapper.apply(savedTimetable);
    }
}
