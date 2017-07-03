package in.ac.amu.zhcet.configuration;

import in.ac.amu.zhcet.configuration.security.RoleWiseSuccessHandler;
import in.ac.amu.zhcet.data.Roles;
import in.ac.amu.zhcet.data.model.BaseUser;
import in.ac.amu.zhcet.data.service.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailService userDetailsService;

    @Autowired
    public SecurityConfiguration(UserDetailService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationMgr) throws Exception {
        authenticationMgr
                .userDetailsService(userDetailsService)
                .passwordEncoder(BaseUser.PASSWORD_ENCODER);
    }

    @Bean
    RoleWiseSuccessHandler roleWiseSuccessHandler() {
        return new RoleWiseSuccessHandler();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests()
                .antMatchers("/", "/attendance", "/courses").permitAll()
                .antMatchers("/student").hasAuthority(Roles.STUDENT).anyRequest().authenticated()
                .antMatchers("/dean").hasAuthority(Roles.DEAN_ADMIN).anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").permitAll()
                .failureUrl("/login?error")
                .usernameParameter("username").passwordParameter("password")
                .successHandler(roleWiseSuccessHandler())
                .and()
                .logout().logoutSuccessUrl("/login?logout");

        httpSecurity.csrf().disable();
        httpSecurity.headers().frameOptions().disable();
    }

}
