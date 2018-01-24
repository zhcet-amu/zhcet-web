package amu.zhcet.firebase.auth;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Authentication Filter to transform request to an authentication and pass through Authentication Managers
 * A special type {@link FirebaseAuthenticationToken} is created from the POST request header {@link #FIREBASE_SECURITY_TOKEN_KEY}
 * Filter operates at the endpoint {@code /login:firebase}
 */
@Slf4j
@Component
public class FirebaseAutheticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String FIREBASE_SECURITY_TOKEN_KEY = "firebase_token";

    public FirebaseAutheticationFilter() {
        super(new AntPathRequestMatcher("/login:firebase", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        String token = Strings.nullToEmpty(obtainFirebaseToken(request)).trim();

        FirebaseAuthenticationToken authRequest = new FirebaseAuthenticationToken(token);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private String obtainFirebaseToken(HttpServletRequest request) {
        return request.getParameter(FIREBASE_SECURITY_TOKEN_KEY);
    }

    private void setDetails(HttpServletRequest request, FirebaseAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    @Override
    @Autowired(required = false)
    public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        super.setAuthenticationDetailsSource(authenticationDetailsSource);
    }

    @Override
    @Autowired(required = false)
    public void setAuthenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
        super.setAuthenticationFailureHandler(failureHandler);
    }

    @Override
    @Autowired(required = false)
    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler successHandler) {
        super.setAuthenticationSuccessHandler(successHandler);
    }

    @Override
    @Autowired(required = false)
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        super.setApplicationEventPublisher(eventPublisher);
    }

    @Override
    @Autowired(required = false)
    public void setRememberMeServices(RememberMeServices rememberMeServices) {
        super.setRememberMeServices(rememberMeServices);
    }

    @Override
    @Autowired(required = false)
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }
}
