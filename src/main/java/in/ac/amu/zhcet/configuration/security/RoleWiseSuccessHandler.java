package in.ac.amu.zhcet.configuration.security;

import in.ac.amu.zhcet.data.Roles;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

public class RoleWiseSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        new DefaultRedirectStrategy().sendRedirect(request, response, determineTargetUrl(authentication));
    }

    private String determineTargetUrl(Authentication authentication) {
        Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (authorities.contains(Roles.DEAN_ADMIN))
            return "/dean";
        else if (authorities.contains(Roles.DEPARTMENT_ADMIN))
            return "/department";
        else
            return "/student";
    }

}
