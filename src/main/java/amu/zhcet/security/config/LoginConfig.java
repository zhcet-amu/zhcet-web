package amu.zhcet.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class LoginConfig {

    private final AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource;
    private final AuthenticationFailureHandler authenticationFailureHandler;

    public LoginConfig(AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource, AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationDetailsSource = authenticationDetailsSource;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    public void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/login").permitAll()
                .authenticationDetailsSource(authenticationDetailsSource)
                .failureHandler(authenticationFailureHandler)

                .and()
                .logout().permitAll()
                .logoutSuccessUrl("/login?logout");
    }

}
