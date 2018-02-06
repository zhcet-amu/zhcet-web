package amu.zhcet.auth;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

public class Auditor implements AuditorAware<String> {

    public static Optional<Authentication> getLoggedInAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    public static Optional<UserAuth> getLoggedInUser() {
        return getLoggedInAuthentication().flatMap(authentication -> Optional.of((UserAuth) authentication.getPrincipal()));
    }

    public static Optional<String> getLoggedInUsernameOptional() {
        return getLoggedInAuthentication()
                .filter(authentication -> authentication.getPrincipal() instanceof User)
                .map(authentication -> (User) authentication.getPrincipal())
                .map(User::getUsername);
    }

    public static String getLoggedInUsername() {
        return getLoggedInUsernameOptional().orElse("UNAUTHENTICATED");
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        return getLoggedInUsernameOptional();
    }
}
