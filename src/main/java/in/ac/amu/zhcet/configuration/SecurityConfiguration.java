package in.ac.amu.zhcet.configuration;

import in.ac.amu.zhcet.configuration.security.RoleWiseSuccessHandler;
import in.ac.amu.zhcet.data.Roles;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.service.token.PersistentTokenService;
import in.ac.amu.zhcet.service.user.UserDetailService;
import in.ac.amu.zhcet.service.user.Auditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailService userDetailsService;
    private final PersistentTokenService persistentTokenService;

    @Autowired
    public SecurityConfiguration(UserDetailService userDetailsService, PersistentTokenService persistentTokenService) {
        this.userDetailsService = userDetailsService;
        this.persistentTokenService = persistentTokenService;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationMgr) throws Exception {
        authenticationMgr
                .userDetailsService(userDetailsService)
                .passwordEncoder(UserAuth.PASSWORD_ENCODER);
    }

    @Bean
    RoleWiseSuccessHandler roleWiseSuccessHandler() {
        return new RoleWiseSuccessHandler();
    }

    @Bean
    AuditorAware<String> auditorAware() {
        return new Auditor();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests()
                .antMatchers("/", "/courses").permitAll()
                .antMatchers("/profile/**").authenticated()
                .antMatchers("/attendance/**").hasAuthority(Roles.STUDENT)
                .antMatchers("/dean/**").hasAuthority(Roles.DEAN_ADMIN)
                .antMatchers("/department/**").hasAuthority(Roles.DEPARTMENT_ADMIN)
                .antMatchers("/faculty/**").hasAuthority(Roles.FACULTY)
                .and()
                .formLogin().loginPage("/login").permitAll()
                .failureUrl("/login?error")
                .usernameParameter("username").passwordParameter("password")
                .successHandler(roleWiseSuccessHandler())
                .and()
                .logout().logoutSuccessUrl("/")
                .and()
                .rememberMe()
                    .rememberMeCookieName("zhcet-remember-me")
                    .tokenValiditySeconds(24*60*60)
                    .tokenRepository(persistentTokenService)
                .and()
                .csrf().ignoringAntMatchers("/console/**")
                .and()
                .headers().frameOptions().disable();
    }

}
