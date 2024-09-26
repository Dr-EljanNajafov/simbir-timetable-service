CREATE TABLE IF NOT EXISTS timetable
(
    id          BIGSERIAL PRIMARY KEY,
    hospital_id BIGINT       NOT NULL,
    doctor_id   BIGINT       NOT NULL,
    from_time   TIMESTAMP    NOT NULL,
    to_time     TIMESTAMP    NOT NULL,
    room        VARCHAR(255) NOT NULL,
    CONSTRAINT check_duration CHECK (EXTRACT(EPOCH FROM (to_time - from_time)) / 60 <= 720), -- Ограничение: разница между from и to не более 12 часов
    CONSTRAINT check_time_intervals CHECK (EXTRACT(MINUTE FROM from_time) % 30 = 0 AND
                                           EXTRACT(MINUTE FROM to_time) % 30 =
                                           0),                                               -- Минуты должны быть кратны 30, секунды всегда 0
    CONSTRAINT check_from_to CHECK (to_time > from_time),                                    -- Ограничение: to должно быть больше from

    -- Внешние ключи с каскадным удалением
    CONSTRAINT fk_hospital FOREIGN KEY (hospital_id) REFERENCES hospital (id) ON DELETE CASCADE,
    CONSTRAINT fk_doctor FOREIGN KEY (doctor_id) REFERENCES doctor (id) ON DELETE CASCADE
);