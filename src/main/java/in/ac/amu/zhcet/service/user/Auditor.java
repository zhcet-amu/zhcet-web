package in.ac.amu.zhcet.service.user;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class Auditor implements AuditorAware<String> {

    public static Authentication getLoggedInAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static CustomUser getLoggedInUser() {
        return (CustomUser) getLoggedInAuthentication().getPrincipal();
    }

    public static String getLoggedInUsername() {
        Authentication authentication = getLoggedInAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User))
            return "UNAUTHENTICATED";

        String username = ((User) authentication.getPrincipal()).getUsername();

        return username == null ? "UNAUTHENTICATED" : username;
    }

    @Override
    public String getCurrentAuditor() {
        return getLoggedInUsername();
    }
}
