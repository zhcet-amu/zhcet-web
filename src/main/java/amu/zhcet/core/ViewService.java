package amu.zhcet.core;

import amu.zhcet.common.utils.StringUtils;
import amu.zhcet.auth.UserAuth;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.user.Gender;
import amu.zhcet.data.user.Role;
import amu.zhcet.security.permission.PermissionManager;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ViewService {

    private final SessionRegistry sessionRegistry;
    private final PermissionManager permissionManager;

    @Autowired
    public ViewService(SessionRegistry sessionRegistry, PermissionManager permissionManager) {
        this.sessionRegistry = sessionRegistry;
        this.permissionManager = permissionManager;
    }

    public List<UserAuth> getUsersFromSessionRegistry() {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(u -> !sessionRegistry.getAllSessions(u, false).isEmpty())
                .map(object -> ((UserAuth) object))
                .collect(Collectors.toList());
    }

    public String getClassForCourse(Course course) {
        if (course.getSemester() == null)
            return "tag-default";

        switch (course.getSemester()) {
            case 3:
                return "tag-danger";
            case 4:
                return "tag-info";
            case 5:
                return "bg-pink";
            case 6:
                return "bg-orange";
            case 7:
                return "tag-primary";
            case 8:
                return "tag-success";
            default:
                return "tag-default";
        }
    }

    public String getClassForGender(Gender gender) {
        if (gender == null) return "";
        return gender.equals(Gender.M) ? "blue-dark" : "pink-dark";
    }

    public String getAvatarUrl(String url) {
        if (Strings.isNullOrEmpty(url))
            return "https://zhcet-web-amu.firebaseapp.com/static/img/account.svg";

        return url;
    }

    public String roleFilter(String role) {
        return StringUtils.capitalizeFirst(String.join(" ",
                role.toLowerCase()
                .replace("role_", "")
                .split("_")));
    }

    public boolean containsRole(List<String> roles, Role role) {
        return roles.stream()
                .anyMatch(anyRole -> anyRole.equals(role.toString()));
    }

    public List<String> getOnlyReachableRoles(List<String> roles) {
        return permissionManager.authorities(roles).stream()
                .map(GrantedAuthority::getAuthority)
                .distinct()
                .filter(role -> !roles.contains(role))
                .collect(Collectors.toList());
    }

}
