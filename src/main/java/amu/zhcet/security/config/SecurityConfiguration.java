package amu.zhcet.security.config;

import amu.zhcet.data.user.Role;
import amu.zhcet.firebase.auth.FirebaseAuthenticationProvider;
import amu.zhcet.firebase.auth.FirebaseAutheticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
class SecurityConfiguration extends WebSecurityConfigurerAdapter {


    private final FirebaseAutheticationFilter firebaseAutheticationFilter;
    private final LoginConfig loginConfig;
    private final RememberMeConfig rememberMeConfig;
    private final SessionConfig sessionConfig;

    @Autowired
    public SecurityConfiguration(FirebaseAutheticationFilter firebaseAutheticationFilter,
                                 LoginConfig loginConfig,
                                 RememberMeConfig rememberMeConfig,
                                 SessionConfig sessionConfig) {
        this.firebaseAutheticationFilter = firebaseAutheticationFilter;
        this.loginConfig = loginConfig;
        this.rememberMeConfig = rememberMeConfig;
        this.sessionConfig = sessionConfig;
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
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(firebaseAutheticationFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeRequests()

                .requestMatchers(EndpointRequest.toAnyEndpoint())
                .hasRole(Role.DEVELOPMENT_ADMIN.name())

                .antMatchers("/profile/**").authenticated()

                .antMatchers("/dashboard/student/**")
                .hasAuthority(Role.STUDENT.toString())

                .antMatchers("/dashboard/**").authenticated()

                .antMatchers("/notifications/{id}/**")
                .access("@permissionManager.checkNotificationRecipient(authentication, #id)")
                .antMatchers("/notifications/**")
                .hasAnyAuthority(Role.VERIFIED_USER.toString())

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

                .antMatchers("/").permitAll();

        loginConfig.configure(http);
        rememberMeConfig.configure(http);
        sessionConfig.configure(http);
    }
}
