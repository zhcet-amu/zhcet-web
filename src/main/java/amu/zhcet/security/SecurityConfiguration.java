package amu.zhcet.security;

import amu.zhcet.auth.login.persistent.PersistentTokenService;
import amu.zhcet.data.user.Role;
import amu.zhcet.firebase.auth.FirebaseAuthenticationProvider;
import amu.zhcet.firebase.auth.FirebaseAutheticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Configuration
@EnableWebSecurity
class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final PersistentTokenService persistentTokenService;
    private final AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource;
    private final SessionRegistry sessionRegistry;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final FirebaseAutheticationFilter firebaseAutheticationFilter;

    @Autowired
    public SecurityConfiguration(
            PersistentTokenService persistentTokenService,
            AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource,
            SessionRegistry sessionRegistry,
            AuthenticationFailureHandler authenticationFailureHandler,
            FirebaseAutheticationFilter firebaseAutheticationFilter) {
        this.persistentTokenService = persistentTokenService;
        this.authenticationDetailsSource = authenticationDetailsSource;
        this.sessionRegistry = sessionRegistry;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.firebaseAutheticationFilter = firebaseAutheticationFilter;
    }

    @Autowired
    public void configureFirebaseAuthentication(AuthenticationManagerBuilder authBuilder, FirebaseAuthenticationProvider firebaseAuthenticationProvider) {
        authBuilder.authenticationProvider(firebaseAuthenticationProvider);
    }

    @Autowired
    public void configureCustomAuthentication(AuthenticationManagerBuilder authBuilder, DaoAuthenticationProvider daoAuthenticationProvider) {
        authBuilder.authenticationProvider(daoAuthenticationProvider);
    }

    @Autowired
    public void configureEventPublisher(AuthenticationManagerBuilder authBuilder, AuthenticationEventPublisher authenticationEventPublisher) {
        authBuilder.authenticationEventPublisher(authenticationEventPublisher);
    }

    @Override
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .addFilterBefore(firebaseAutheticationFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeRequests()

                .requestMatchers(EndpointRequest.toAnyEndpoint())
                .hasRole(Role.DEVELOPMENT_ADMIN.name())

                .antMatchers("/").permitAll()

                .antMatchers("/profile/**").authenticated()

                .antMatchers("/dashboard/**").authenticated()

                .antMatchers("/dashboard/student/**")
                .hasAuthority(Role.STUDENT.toString())

                .antMatchers("/notifications/{id}/**")
                .access("@permissionManager.checkNotificationRecipient(authentication, #id)")
                .antMatchers("/notifications/**")
                .authenticated()

                .antMatchers("/management/notifications/{id}/**")
                .access("@permissionManager.checkNotificationCreator(authentication, #id)")
                .antMatchers("/management/**")
                .hasAuthority(Role.TEACHING_STAFF.toString())

                .antMatchers("/admin/dean/**")
                .hasAuthority(Role.DEAN_ADMIN.toString())

                .antMatchers("/admin/department/courses/{course}/**",
                        "/admin/department/floated/{course}/**",
                        "/admin/department/float/{course}/**")
                .access("@permissionManager.checkCourse(authentication, #course)")

                .antMatchers("/admin/department/{department}/**")
                .access("@permissionManager.checkDepartment(authentication, #department)")
                .antMatchers("/admin/department/**")
                .hasAuthority(Role.DEPARTMENT_ADMIN.toString())

                .antMatchers("/admin/faculty/**")
                .hasAuthority(Role.FACULTY.toString())

                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .authenticationDetailsSource(authenticationDetailsSource)
                .failureHandler(authenticationFailureHandler)

                .and()
                .logout().permitAll()
                .logoutSuccessUrl("/login?logout")

                .and()
                .rememberMe()
                .rememberMeCookieName("zhcet-remember-me")
                .tokenValiditySeconds(24 * 60 * 60)
                .tokenRepository(persistentTokenService)

                .and()
                .sessionManagement()
                .maximumSessions(1)
                .sessionRegistry(sessionRegistry)
                .expiredUrl("/login?expired");
    }
}
