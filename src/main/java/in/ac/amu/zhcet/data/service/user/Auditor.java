package in.ac.amu.zhcet.data.service.user;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class Auditor implements AuditorAware<String> {
    @Override
    public String getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            return "UNAUTHENTICATED";

        String username = ((User) authentication.getPrincipal()).getUsername();

        return username == null ? "UNAUTHENTICATED" : username;
    }
}
