package amu.zhcet.core.auth;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

public class Auditor implements AuditorAware<String> {

    public static Optional<Authentication> getLoggedInAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    public static Optional<CustomUser> getLoggedInUser() {
        return getLoggedInAuthentication().flatMap(authentication -> Optional.of((CustomUser) authentication.getPrincipal()));
    }

    public static String getLoggedInUsername() {
        Optional<Authentication> authenticationOptional = getLoggedInAuthentication();
        if (!authenticationOptional.isPresent() || !(authenticationOptional.get().getPrincipal() instanceof User))
            return "UNAUTHENTICATED";

        String username = ((User) authenticationOptional.get().getPrincipal()).getUsername();
        return username == null ? "UNAUTHENTICATED" : username;
    }

    @Override
    public String getCurrentAuditor() {
        return getLoggedInUsername();
    }
}
