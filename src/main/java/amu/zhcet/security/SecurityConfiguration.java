package amu.zhcet.security;

import amu.zhcet.core.auth.Auditor;
import amu.zhcet.core.auth.CustomAuthenticationDetails;
import amu.zhcet.core.auth.UserDetailService;
import amu.zhcet.core.auth.login.handler.UsernameAuthenticationFailureHandler;
import amu.zhcet.core.auth.login.persistent.PersistentTokenService;
import amu.zhcet.data.user.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    AuditorAware<String> auditorAware() {
        return new Auditor();
    }

    @Bean
    SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    AuthenticationFailureHandler authenticationFailureHandler() {
        return new UsernameAuthenticationFailureHandler();
    }

    @Bean
    AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource() {
        return CustomAuthenticationDetails::new;
    }

    @Bean
    RoleVoter roleVoter(RoleHierarchy roleHierarchy) {
        return new RoleHierarchyVoter(roleHierarchy);
    }

    @Bean
    protected DefaultWebSecurityExpressionHandler webExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
        defaultWebSecurityExpressionHandler.setRoleHierarchy(roleHierarchy);
        return defaultWebSecurityExpressionHandler;
    }

    /**
     * Spring Security Context is limited to local thread and hence every asynchronous method gets
     * null logged in user info. We modify the SecurityContextHolder here to leverage auditing capabilities
     * in asynchronous methods as well by enabling MODE_INHERITABLETHREADLOCAL
     * @return MethodInvokingFactoryBean
     */
    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean() {
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setTargetClass(SecurityContextHolder.class);
        methodInvokingFactoryBean.setTargetMethod("setStrategyName");
        methodInvokingFactoryBean.setArguments((Object[]) new String[]{ SecurityContextHolder.MODE_INHERITABLETHREADLOCAL });
        return methodInvokingFactoryBean;
    }

    @Configuration
    static class SecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        private final UserDetailService userDetailsService;
        private final PersistentTokenService persistentTokenService;
        private final AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource;
        private final DefaultWebSecurityExpressionHandler securityExpressionHandler;
        private final SessionRegistry sessionRegistry;

        @Autowired
        public SecurityConfigurerAdapter(
                @Lazy UserDetailService userDetailsService,
                @Lazy PersistentTokenService persistentTokenService,
                AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource,
                DefaultWebSecurityExpressionHandler securityExpressionHandler,
                SessionRegistry sessionRegistry
        ) {
            this.userDetailsService = userDetailsService;
            this.persistentTokenService = persistentTokenService;
            this.authenticationDetailsSource = authenticationDetailsSource;
            this.securityExpressionHandler = securityExpressionHandler;
            this.sessionRegistry = sessionRegistry;
        }

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder authenticationMgr, PasswordEncoder passwordEncoder) throws Exception {
            authenticationMgr
                    .userDetailsService(userDetailsService)
                    .passwordEncoder(passwordEncoder);
        }

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity
                    .authorizeRequests()
                    .expressionHandler(securityExpressionHandler)
                        .antMatchers("/").permitAll()
                        .antMatchers("/profile/**").authenticated()
                        .antMatchers("/notifications/{id}/**")
                    .access("@permissionManager.checkNotificationRecipient(authentication, #id)")
                        .antMatchers("/notifications/**").authenticated()
                        .antMatchers("/student/**").hasAuthority(Role.STUDENT.toString())
                        .antMatchers("/admin/dean/**").hasAuthority(Role.DEAN_ADMIN.toString())
                        .antMatchers("/department/{department}/courses/{course}/**")
                    .access("@permissionManager.checkCourse(authentication, #department, #course)")
                        .antMatchers("/department/{department}/floated/{course}/**")
                    .access("@permissionManager.checkCourse(authentication, #department, #course)")
                        .antMatchers("/department/{department}/**")
                    .access("@permissionManager.checkDepartment(authentication, #department)")
                        .antMatchers("/department/**").hasAuthority(Role.DEPARTMENT_ADMIN.toString())
                        .antMatchers("/faculty/**").hasAuthority(Role.FACULTY.toString())
                        .antMatchers("/management/notifications/{id}/**")
                    .access("@permissionManager.checkNotificationCreator(authentication, #id)")
                        .antMatchers("/management/**").hasAuthority(Role.TEACHING_STAFF.toString())
                    .and()
                    .formLogin()
                        .authenticationDetailsSource(authenticationDetailsSource)
                        .loginPage("/login").permitAll()
                        .failureHandler(new UsernameAuthenticationFailureHandler("/login?error"))
                        .usernameParameter("username")
                        .passwordParameter("password")
                    .and()
                        .logout().logoutSuccessUrl("/login?logout").permitAll()
                    .and()
                    .rememberMe()
                        .rememberMeCookieName("zhcet-remember-me")
                        .tokenValiditySeconds(24 * 60 * 60)
                        .tokenRepository(persistentTokenService)
                    .and()
                        .sessionManagement().maximumSessions(1).sessionRegistry(sessionRegistry);
        }
    }

}
