package com.github.simbir_timetable_service.client;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TokenValidationInterceptor implements HandlerInterceptor {

    private final AccountServiceClient accountServiceClient;

    @Autowired
    public TokenValidationInterceptor(AccountServiceClient accountServiceClient) {
        this.accountServiceClient = accountServiceClient;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();

        // Добавьте отладочный вывод для проверки CSRF-токена
        String csrfToken = request.getHeader("X-CSRF-Token");
        log.info("CSRF Token: {}", csrfToken);

        // Исключаем Swagger UI из валидации токена
        if (requestUri.startsWith("/swagger-ui") || requestUri.startsWith("/v3/api-docs")) {
            return true; // Пропускаем валидацию
        }

        String token = request.getHeader("Authorization");
        log.info("Интерцептор валидации токена вызван для запроса: {}", request.getRequestURI());

        if (token == null || !token.startsWith("Bearer ")) {
            log.warn("Токен авторизации отсутствует или неправильно отформатирован");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Токен авторизации отсутствует или неправильно отформатирован");
            return false;
        }

        token = token.substring(7); // Убираем префикс 'Bearer '

        log.info("Проверка токена: {}", token);

        try {
            ResponseEntity<?> validationResult = accountServiceClient.validateToken(token);
            log.info("Статус ответа валидации токена: {}", validationResult.getStatusCode());

            if (!validationResult.getStatusCode().is2xxSuccessful()) {
                log.warn("Токен недействителен или истек");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Токен недействителен или истек");
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка при валидации токена", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write("Ошибка при валидации токена: " + e.getMessage());
            return false;
        }

        log.info("Token is valid. Proceeding with request.");
        return true;
    }
}
