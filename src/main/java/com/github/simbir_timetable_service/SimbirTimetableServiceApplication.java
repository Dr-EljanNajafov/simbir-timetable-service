package com.github.simbir_timetable_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SimbirTimetableServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimbirTimetableServiceApplication.class, args);
    }

}
