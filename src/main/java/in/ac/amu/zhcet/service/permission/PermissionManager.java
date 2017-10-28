package in.ac.amu.zhcet.service.permission;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.type.Roles;
import in.ac.amu.zhcet.service.user.CustomUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
public class PermissionManager {

    public static List<GrantedAuthority> authorities(List<String> roles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        // Add normal stored roles
        for (String role: roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
            grantedAuthorities.addAll(getExtraPermissions(role));
        }

        return grantedAuthorities;
    }

    private static List<GrantedAuthority> getExtraPermissions(String role) {
        List<GrantedAuthority> permissions = new ArrayList<>();

        switch (role) {
            case Roles.SUPER_ADMIN:
                permissions.add(new SimpleGrantedAuthority(Roles.DEAN_ADMIN));
                permissions.add(new SimpleGrantedAuthority(Roles.MANAGEMENT_ADMIN));
                permissions.add(new SimpleGrantedAuthority(Roles.DEPARTMENT_SUPER_ADMIN));
                permissions.add(new SimpleGrantedAuthority(Roles.DEPARTMENT_ADMIN));
                permissions.add(new SimpleGrantedAuthority(Roles.SUPER_FACULTY));
                permissions.add(new SimpleGrantedAuthority(Roles.FACULTY));
                break;
            case Roles.DEPARTMENT_SUPER_ADMIN:
                permissions.add(new SimpleGrantedAuthority(Roles.DEPARTMENT_ADMIN));
                break;
            case Roles.SUPER_FACULTY:
                permissions.add(new SimpleGrantedAuthority(Roles.FACULTY));
                break;
        }

        return permissions;
    }

    public static boolean hasPermission(Collection<? extends GrantedAuthority> authorities, String permission) {
        return authorities.stream().map(GrantedAuthority::getAuthority).anyMatch(authority -> authority.equals(permission));
    }

    public static boolean hasPermissionOfDepartment(Authentication user, Department department) {
        if (hasPermission(user.getAuthorities(), Roles.DEPARTMENT_SUPER_ADMIN))
            return true;

        return ((CustomUser) user.getPrincipal()).getDepartment().equals(department);
    }

    public static boolean hasPermissionOfDepartmentAndCourse(Authentication user, Department department, Course course) {
        if (!department.equals(course.getDepartment()))
            return false;

        return hasPermissionOfDepartment(user, department);
    }

}
