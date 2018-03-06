package amu.zhcet.event;

import amu.zhcet.auth.AuthManager;
import amu.zhcet.auth.AuthService;
import amu.zhcet.auth.password.change.PasswordChangeEvent;
import amu.zhcet.auth.verification.EmailVerifiedEvent;
import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class AuthEventListener {

    private final AuthManager authManager;
    private final AuthService authService;
    private final UserService userService;

    public AuthEventListener(AuthManager authManager, AuthService authService, UserService userService) {
        this.authManager = authManager;
        this.authService = authService;
        this.userService = userService;
    }

    @EventListener(condition = "#emailVerifiedEvent.verified")
    public void emailVerified(EmailVerifiedEvent emailVerifiedEvent) {
        log.debug("User {} email {} was verified", emailVerifiedEvent.getUser().getUserId(), emailVerifiedEvent.getUser().getEmail());
        assignPendingRoles(emailVerifiedEvent.getUser());
    }

    @EventListener
    public void passwordChanged(PasswordChangeEvent passwordChangeEvent) {
        log.debug("User {} changed its password", passwordChangeEvent.getUser());
        assignPendingRoles(passwordChangeEvent.getUser());
    }

    private void assignPendingRoles(User user) {
        log.debug("Checking if worth assigning pending roles");
        if (!AuthService.isFullyAuthenticated(user)) {
            log.debug("User {} is not yet fully authenticated, skipping assigning roles", user);
        } else {
            List<String> pendingRoles = user.getPendingRoles();

            if (pendingRoles == null) {
                log.debug("No pending roles found for the user. Skipping...");
                return;
            }

            log.info("User {} is fully authenticated now. Assigning pending roles {} to the user", user, pendingRoles);

            List<String> oldRoles = user.getRoles();
            List<String> newRolesTemp = new ArrayList<>(oldRoles);
            newRolesTemp.addAll(pendingRoles);

            Set<String> newRoles = authService.getOptimalRoles(newRolesTemp);

            log.debug("{} + {} = {}", oldRoles, pendingRoles, newRoles);
            user.setRoles(newRoles);
            user.setPendingRoles(null);
            userService.save(user);
            authManager.updateRoles(user);
        }
    }

}
