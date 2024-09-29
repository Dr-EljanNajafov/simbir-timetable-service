package com.github.simbir_timetable_service.util;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404) {
            // Пример кастомной логики для обработки 404
            if (methodKey.contains("HospitalServiceClient#getHospitalById")) {
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Больница не найдена.");
            }
            if (methodKey.contains("DoctorServiceClient#getDoctorById")) {
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Доктор не найден.");
            }
        }

        return defaultErrorDecoder.decode(methodKey, response);
    }
}