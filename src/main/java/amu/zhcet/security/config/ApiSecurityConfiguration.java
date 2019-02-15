package amu.zhcet.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Order(1)
@Configuration
public class ApiSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/api/**")
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/test/**").permitAll()
                .antMatchers("/api/auth").permitAll()
                .antMatchers("/api/v1/admin/**").hasRole("DEAN_ADMIN")
                .antMatchers("/api/v1/dev/**").hasRole("DEVELOPMENT_ADMIN")
                .anyRequest().fullyAuthenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

}
