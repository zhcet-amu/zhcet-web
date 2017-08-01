package in.ac.amu.zhcet.data.service.user;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class Auditor implements AuditorAware<String> {
    @Override
    public String getCurrentAuditor() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    }
}
