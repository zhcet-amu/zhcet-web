package amu.zhcet.auth;

import amu.zhcet.common.utils.StringUtils;
import amu.zhcet.data.user.Role;
import amu.zhcet.data.user.User;
import amu.zhcet.security.permission.PermissionManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Special helper service for common authorization related methods
 * Methods are present as both static and instance methods as instance methods
 * are used in Thymeleaf Templates
 */
@Service
public class AuthService {

    private final SessionRegistry sessionRegistry;
    private final PermissionManager permissionManager;

    public AuthService(SessionRegistry sessionRegistry, PermissionManager permissionManager) {
        this.sessionRegistry = sessionRegistry;
        this.permissionManager = permissionManager;
    }

    public static boolean isFullyAuthenticated(User user) {
        return user.isPasswordChanged() && user.isEmailVerified() && user.getEmail() != null;
    }

    public static boolean isFullyAuthenticated(UserAuth user) {
        return user.isPasswordChanged() && user.isEmailVerified() && user.getEmail() != null;
    }

    public static List<String> getPendingTasks(User user) {
        List<String> pendingTasks = new ArrayList<>();
        if (user.getEmail() == null)
            pendingTasks.add("Register your email");
        if (!user.isEmailVerified())
            pendingTasks.add("Verify your email");
        if (!user.isPasswordChanged())
            pendingTasks.add("Change your password from default one");
        return pendingTasks;
    }

    public List<UserAuth> getUsersFromSessionRegistry() {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(u -> !sessionRegistry.getAllSessions(u, false).isEmpty())
                .map(object -> ((UserAuth) object))
                .collect(Collectors.toList());
    }

    public static String roleFilter(String role) {
        return StringUtils.capitalizeFirst(String.join(" ",
                role.toLowerCase()
                        .replace("role_", "")
                        .split("_")));
    }

    public static boolean containsRole(List<String> roles, Role role) {
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

    public Set<String> getOptimalRoles(List<String> roles) {
        if (roles == null)
            roles = new ArrayList<>();

        Set<String> reachableRoles = roles.stream()
                .map(Collections::singletonList)
                .map(this::getOnlyReachableRoles)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        return roles.stream()
                .filter(role -> !reachableRoles.contains(role))
                .collect(Collectors.toSet());
    }

}
