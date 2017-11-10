package in.ac.amu.zhcet.configuration.security;

import in.ac.amu.zhcet.configuration.security.login.CustomAuthenticationDetails;
import in.ac.amu.zhcet.configuration.security.login.RoleWiseSuccessHandler;
import in.ac.amu.zhcet.data.type.Roles;
import in.ac.amu.zhcet.service.user.Auditor;
import in.ac.amu.zhcet.service.user.UserDetailService;
import in.ac.amu.zhcet.service.user.auth.PersistentTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailService userDetailsService;
    private final PersistentTokenService persistentTokenService;

    @Autowired
    public SecurityConfiguration(@Lazy UserDetailService userDetailsService, PersistentTokenService persistentTokenService) {
        this.userDetailsService = userDetailsService;
        this.persistentTokenService = persistentTokenService;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationMgr, PasswordEncoder passwordEncoder) throws Exception {
        authenticationMgr
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Bean
    RoleWiseSuccessHandler roleWiseSuccessHandler() {
        return new RoleWiseSuccessHandler();
    }

    @Bean
    AuditorAware<String> auditorAware() {
        return new Auditor();
    }

    @Bean
    SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource() {
        return CustomAuthenticationDetails::new;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/profile/**").authenticated()
                .antMatchers("/notifications/**").authenticated()
                .antMatchers("/student/**").hasAuthority(Roles.STUDENT)
                .antMatchers("/dean/**").hasAuthority(Roles.DEAN_ADMIN)
                .antMatchers("/department/**").hasAuthority(Roles.DEPARTMENT_ADMIN)
                .antMatchers("/faculty/**").hasAuthority(Roles.FACULTY)
                .antMatchers("/notification/**").hasAnyAuthority(Roles.FACULTY, Roles.DEPARTMENT_ADMIN, Roles.DEPARTMENT_ADMIN)
                .and()
                    .formLogin()
                    .authenticationDetailsSource(authenticationDetailsSource())
                    .loginPage("/login").permitAll()
                    .failureUrl("/login?error")
                    .usernameParameter("username").passwordParameter("password")
                    .successHandler(roleWiseSuccessHandler())
                .and()
                    .logout().logoutSuccessUrl("/login?logout").permitAll()
                .and()
                    .rememberMe()
                    .rememberMeCookieName("zhcet-remember-me")
                    .tokenValiditySeconds(24*60*60)
                    .tokenRepository(persistentTokenService)
                .and()
                    .sessionManagement().maximumSessions(1).sessionRegistry(sessionRegistry());
    }

}
