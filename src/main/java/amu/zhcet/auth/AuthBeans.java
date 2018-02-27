package amu.zhcet.auth;

import amu.zhcet.auth.login.handler.UsernameAuthenticationFailureHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class AuthBeans {

    @Bean
    AuditorAware<String> auditorAware() {
        return new Auditor();
    }

    @Bean
    AuthenticationFailureHandler authenticationFailureHandler() {
        return new UsernameAuthenticationFailureHandler("/login?error");
    }

    @Bean
    AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource() {
        return CustomAuthenticationDetails::new;
    }

}
