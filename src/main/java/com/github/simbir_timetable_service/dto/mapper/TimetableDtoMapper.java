package com.github.simbir_timetable_service.dto.mapper;

import com.github.simbir_timetable_service.dto.TimetableDto;
import com.github.simbir_timetable_service.timetable.Timetable;
import org.springframework.stereotype.Component;
import java.util.function.Function;

@Component
public class TimetableDtoMapper implements Function<Timetable, TimetableDto> {

    @Override
    public TimetableDto apply(Timetable timetable) {
        return new TimetableDto(
                timetable.getHospitalId(),
                timetable.getDoctorId(),
                timetable.getFrom(),
                timetable.getTo(),
                timetable.getRoom(),
                timetable.getPatientId()
        );
    }
}
