package amu.zhcet.auth.login.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.security.AbstractAuthorizationAuditListener;
import org.springframework.boot.actuate.security.AuthenticationAuditListener;
import org.springframework.boot.actuate.security.AuthorizationAuditListener;
import org.springframework.security.access.event.AbstractAuthorizationEvent;
import org.springframework.security.access.event.AuthenticationCredentialsNotFoundEvent;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class PathAuthorizationAuditListener extends AbstractAuthorizationAuditListener {

    public static final String SUCCESS = "AUTHENTICATION_SUCCESS";

    @Override
    public void onApplicationEvent(AbstractAuthorizationEvent event) {
        if (event instanceof AuthenticationCredentialsNotFoundEvent) {
            onAuthenticationCredentialsNotFoundEvent(
                    (AuthenticationCredentialsNotFoundEvent) event);
        } else if (event instanceof AuthorizationFailureEvent) {
            onAuthorizationFailureEvent((AuthorizationFailureEvent) event);
        }
    }

    private void onAuthenticationCredentialsNotFoundEvent(AuthenticationCredentialsNotFoundEvent event) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", event.getCredentialsNotFoundException().getClass().getName());
        data.put("message", event.getCredentialsNotFoundException().getMessage());
        if (event.getSource() instanceof FilterInvocation)
            data.put("requestUrl", ((FilterInvocation)event.getSource()).getRequestUrl());
        else if (event.getSource() instanceof ReflectiveMethodInvocation)
            data.put("source", event.getSource());
        publish(new AuditEvent("<unknown>", AuthenticationAuditListener.AUTHENTICATION_FAILURE, data));
    }

    private void onAuthorizationFailureEvent(AuthorizationFailureEvent event) {
        Map<String, Object> data = new HashMap<>();
        data.put("authorities", event.getAuthentication().getAuthorities());
        data.put("type", event.getAccessDeniedException().getClass().getName());
        data.put("message", event.getAccessDeniedException().getMessage());
        if (event.getSource() instanceof FilterInvocation)
            data.put("requestUrl", ((FilterInvocation)event.getSource()).getRequestUrl());
        else if (event.getSource() instanceof ReflectiveMethodInvocation)
            data.put("source", event.getSource());
        if (event.getAuthentication().getDetails() != null) {
            data.put("details", event.getAuthentication().getDetails());
        }

        publish(new AuditEvent(event.getAuthentication().getName(), AuthorizationAuditListener.AUTHORIZATION_FAILURE, data));
    }
}