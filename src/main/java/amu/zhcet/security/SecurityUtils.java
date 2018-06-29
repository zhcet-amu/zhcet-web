package amu.zhcet.security;

import amu.zhcet.auth.AuthManager;
import amu.zhcet.security.permission.PermissionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class SecurityUtils {

    private static List<String> STALE_AUTHORITIES = Collections.singletonList("PASSWORD_CHANGE_PRIVILEGE");

    // Prevent instantiation of Util class
    private SecurityUtils() {}

    public static boolean isRememberMe(Authentication authentication) {
        return authentication != null && authentication.getClass().isAssignableFrom(RememberMeAuthenticationToken.class);
    }

    public static boolean isAnonymous(Authentication authentication) {
        return authentication != null && authentication.getClass().isAssignableFrom(AnonymousAuthenticationToken.class);
    }

    public static boolean isFullyAuthenticated(Authentication authentication) {
        return !(isRememberMe(authentication) || isAnonymous(authentication));
    }

    public static void clearStaleAuthorities(Authentication authentication) {
        boolean isStale = STALE_AUTHORITIES.stream()
                .anyMatch(authority -> PermissionManager.hasPermission(authentication.getAuthorities(), authority));

        if (isStale)
            AuthManager.logout();
    }
}
