package in.ac.amu.zhcet.configuration.security;

import in.ac.amu.zhcet.data.Roles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.util.Set;

@Slf4j
public class RoleWiseSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    // Used indirectly in redirection from login and home controller
    public static String determineTargetUrl(Authentication authentication) {
        Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (authorities.contains(Roles.DEAN_ADMIN))
            return "/dean";
        else if (authorities.contains(Roles.DEPARTMENT_ADMIN))
            return "/department";
        else if (authorities.contains(Roles.FACULTY))
            return "/faculty/courses";
        else if (authorities.contains(Roles.STUDENT))
            return "/student/attendance";
        else
            return "/login";
    }

}
