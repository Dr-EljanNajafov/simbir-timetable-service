package com.github.simbir_timetable_service.config.context;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import java.util.List;

@Data
@Component
@RequestScope
public class UserContext {
    private String token;
    private String username; // идентификатор пользователя
    private List<String> roles; // список ролей

    public void clear() {
        token = null;
        username = null;
        roles = null;
    }
}
