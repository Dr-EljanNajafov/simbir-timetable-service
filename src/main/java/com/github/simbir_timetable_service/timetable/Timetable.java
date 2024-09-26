package com.github.simbir_timetable_service.timetable;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long hospitalId;
    private Long doctorId;

    @Column(name = "\"from_time\"") // Экранирование поля
    private LocalDateTime from;

    @Column(name = "\"to_time\"") // Экранирование поля
    private LocalDateTime to;

    private String room;
}