package com.github.simbir_timetable_service.repository;

import com.github.simbir_timetable_service.timetable.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    boolean existsByDoctorIdAndRoomAndFromAndTo(Long doctorId, String room, LocalDateTime from, LocalDateTime to);

    List<Timetable> deleteTimetableByHospitalId(Long hospitalId);
    List<Timetable> deleteTimetableByDoctorId(Long doctorId);

    List<Timetable> findByHospitalIdAndFromBetween(Long hospitalId, LocalDateTime from, LocalDateTime to);

    List<Timetable> findByDoctorIdAndFromBetween(Long doctorId, LocalDateTime from, LocalDateTime to);

    List<Timetable> findByHospitalIdAndRoomAndFromBetween(Long hospitalId, String room, LocalDateTime from, LocalDateTime to);
}