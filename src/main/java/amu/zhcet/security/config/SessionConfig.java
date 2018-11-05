package amu.zhcet.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;

@Configuration
public class SessionConfig {

    private final SessionRegistry sessionRegistry;

    public SessionConfig(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    public void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .maximumSessions(1)
                .sessionRegistry(sessionRegistry)
                .expiredUrl("/login?expired");
    }

}
