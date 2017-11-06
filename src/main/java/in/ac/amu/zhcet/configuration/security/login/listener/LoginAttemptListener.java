package in.ac.amu.zhcet.configuration.security.login.listener;

import in.ac.amu.zhcet.service.security.login.LoginAttemptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.boot.actuate.security.AuthenticationAuditListener;
import org.springframework.context.event.EventListener;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoginAttemptListener {

    private final LoginAttemptService loginAttemptService;

    @Autowired
    public LoginAttemptListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void auditEventHappened(AuditApplicationEvent auditApplicationEvent) {
        AuditEvent auditEvent = auditApplicationEvent.getAuditEvent();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Principal ").append(auditEvent.getPrincipal()).append(" - ").append(auditEvent.getType());

        WebAuthenticationDetails details = (WebAuthenticationDetails) auditEvent.getData().get("details");

        loginAttemptService.loginAttempt(auditEvent, details);

        if (details != null) {
            stringBuilder.append("\n  Remote IP address: ").append(details.getRemoteAddress());
            stringBuilder.append("\n  Session ID: ").append(details.getSessionId());
        }
        stringBuilder.append("\n  Request URL: ").append(auditEvent.getData().get("requestUrl"));
        stringBuilder.append("\n  Source: ").append(auditEvent.getData().get("source"));

        String message = stringBuilder.toString();
        if (auditEvent.getType().equals(AuthenticationAuditListener.AUTHENTICATION_FAILURE)) {
            log.warn(message);
        } else {
            log.info(message);
        }
    }
}
