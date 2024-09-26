package com.github.simbir_timetable_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenValidationFilter extends OncePerRequestFilter {

    private final AccountServiceClient accountServiceClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        // Исключаем Swagger UI и другие статические ресурсы из валидации токена
        if (requestUri.startsWith("/swagger-ui") || requestUri.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Фильтр валидации токена вызван для запроса: {}", request.getRequestURI());
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            log.warn("Токен авторизации отсутствует или неправильно отформатирован");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Токен авторизации отсутствует или неправильно отформатирован");
            return;
        }

        token = token.substring(7);
        log.info("Проверка токена: {}", token);

        try {
            ResponseEntity<?> validationResult = accountServiceClient.validateToken(token);
            log.info("Статус ответа валидации токена: {}", validationResult.getStatusCode());
            if (!validationResult.getStatusCode().is2xxSuccessful()) {
                log.warn("Токен недействителен или истек");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Токен недействителен или истек");
                return;
            }
        } catch (Exception e) {
            log.error("Ошибка при валидации токена", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write("Ошибка при валидации токена: " + e.getMessage());
            return;
        }

        log.info("Токен действителен. Продолжаем выполнение запроса.");
        filterChain.doFilter(request, response);
    }
}
