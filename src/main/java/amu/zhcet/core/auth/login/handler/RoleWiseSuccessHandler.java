package amu.zhcet.core.auth.login.handler;

import amu.zhcet.data.user.Role;
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

        if (authorities.contains(Role.DEAN_ADMIN.toString()))
            return "/admin/dean";
        else if (authorities.contains(Role.DEVELOPMENT_ADMIN.toString()))
            return "/actuator/health";
        else if (authorities.contains(Role.DEPARTMENT_ADMIN.toString()))
            return "/admin/department";
        else if (authorities.contains(Role.FACULTY.toString()))
            return "/admin/faculty/courses";
        else if (authorities.contains(Role.STUDENT.toString()))
            return "/student/attendance";
        else
            return "/login";
    }

}
