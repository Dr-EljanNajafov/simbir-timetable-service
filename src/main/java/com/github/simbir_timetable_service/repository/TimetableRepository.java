package com.github.simbir_timetable_service.repository;

import com.github.simbir_timetable_service.timetable.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    List<Timetable> findByHospitalId(Long hospitalId);

    List<Timetable> findByDoctorId(Long doctorId);
}