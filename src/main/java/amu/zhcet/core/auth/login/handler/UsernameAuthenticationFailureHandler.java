package amu.zhcet.core.auth.login.handler;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@NoArgsConstructor
public class UsernameAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public static final String USERNAME = UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;

    public UsernameAuthenticationFailureHandler(String failureUrl) {
        super(failureUrl);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        request.getSession().setAttribute(USERNAME, request.getParameter(USERNAME));
        super.onAuthenticationFailure(request, response, exception);
    }
}
