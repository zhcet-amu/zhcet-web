package amu.zhcet.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SecurityBeans {

    @Bean
    SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    AuthenticationEventPublisher authenticationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new DefaultAuthenticationEventPublisher(applicationEventPublisher);
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

    @Bean
    RoleHierarchy roleHierarchy(SecureProperties secureProperties) {
        Map<String, List<String>> roleHierarchyMapping = secureProperties.getRoles().getHierarchy();

        StringWriter roleHierarchyDescriptionBuffer = new StringWriter();
        PrintWriter roleHierarchyDescriptionWriter = new PrintWriter(roleHierarchyDescriptionBuffer);

        for (Map.Entry<String, List<String>> entry : roleHierarchyMapping.entrySet()) {

            String currentRole = entry.getKey();
            List<String> impliedRoles = entry.getValue();

            for (String impliedRole : impliedRoles) {
                String roleMapping = currentRole + " > " + impliedRole;
                roleHierarchyDescriptionWriter.println(roleMapping);
            }
        }

        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(roleHierarchyDescriptionBuffer.toString());
        return roleHierarchy;
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

}
