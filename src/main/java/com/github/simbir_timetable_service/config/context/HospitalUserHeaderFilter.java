package com.github.simbir_timetable_service.config.context;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HospitalUserHeaderFilter implements Filter {

    private final UserContext userContext;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader("Authorization");

        if (token != null) {
            token = token.trim(); // Удаляем лишние пробелы
            userContext.setToken(token);
            log.info("Received Token: {}", token);

            try {
                Claims claims = Jwts.parser()
                        .setSigningKey("9311eceb51e8a2f2d7a5825e6178dcf27d102a8173eb003cf4a0de6bc8e29df0878798a4de52de55ab32c4dcdb123ed35a0bd43c3ac2e499e95a1a95222c3947") // Используйте правильный ключ
                        .parseClaimsJws(token.replace("Bearer ", ""))
                        .getBody();

                userContext.setUsername(claims.getSubject());
                userContext.setRoles(claims.get("roles", List.class));
            } catch (Exception e) {
                log.error("Error decoding token: {}", e.getMessage());
            }
        }
        try {
            chain.doFilter(request, response);
        } finally {
            userContext.clear();
        }
    }
}