package amu.zhcet.auth;

import amu.zhcet.core.error.ErrorUtils;
import amu.zhcet.data.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AuthManager {

    private final UserDetailService userDetailService;

    public AuthManager(UserDetailService userDetailService) {
        this.userDetailService = userDetailService;
    }

    /**
     * Updates the avatar of a logged in user
     * @param userAuth UserAuth for which the avatar is to be updated
     * @param avatarUrl Avatar URL to be updated
     */
    public static void updateAvatar(UserAuth userAuth, String avatarUrl) {
        if (userAuth == null)
            return;
        userAuth.setAvatar(avatarUrl);
    }

    /**
     * Updates roles of a user if it is logged in. Safely returns if the passed in user is not logged in
     * @param user User for which the roles have to be updated containing the new roles
     */
    public void updateRoles(User user) {
        Authentication authentication = getAndValidateAuthentication(user);
        userDetailService.cloneWithRoles(authentication, user);
    }

    /**
     * Grants an anonymous privilege to change password to provided unauthorized user.
     * To be used when it is needed to reset a password, because in this scenario the user is not logged in
     *
     * A new authentication is created and applied
     * @param user User for which the privilege is to be granted
     */
    public void grantChangePasswordPrivilege(User user) {
        log.info("Granting change password privilege to {}", user);
        userDetailService.grantPrivilege(user, "PASSWORD_CHANGE_PRIVILEGE");
    }

    /**
     * Changes the password of the User.
     * This method is raw and all checks for authenticity and credibility of user must be checked by the caller.
     * This includes the check about the password constraints and if the user is allowed to change the password
     *
     * This method just checks if the user provided is actually logged in and updates the user principal in database
     * @param user User whose password is to be updated with the new encoded password set
     */
    public void updatePassword(User user) {
        getAndValidateAuthentication(user);
        userDetailService.cloneWithPassword(user);
    }

    /**
     * Resets the password of the User and logs it out.
     * This method is raw and all checks for authenticity and credibility of user must be checked by the caller.
     * This includes the check about the password constraints and if the user is allowed to reset the password
     *
     * This method just checks if the user provided is actually logged in and updates the user principal in database
     * @param user User whose password is to be reset with the new encoded password set
     */
    public void resetPassword(User user) {
        updatePassword(user);
        logout();
    }

    /**
     * Logs out currently authenticated user
     */
    public static void logout() {
        UserDetailService.logout();
    }

    private Authentication getAndValidateAuthentication(User user) {
        ErrorUtils.requireNonNullUser(user);
        Optional<Authentication> optionalAuthentication = Auditor.getLoggedInAuthentication();
        if (!optionalAuthentication.isPresent()) {
            log.error("Cannot update non authenticated user");
            throw new IllegalStateException("Cannot update non authenticated user");
        }

        Authentication authentication = optionalAuthentication.get();
        if (!extractUsername(authentication).equals(user.getUserId())) {
            log.warn("The user {} and logged in member {} do not match", user.getUserId(), authentication.getName());
            throw new IllegalStateException("User authentication mismatch");
        }

        return authentication;
    }

    private static String extractUsername(Authentication authentication) {
        if (authentication.getPrincipal() instanceof User)
            return ((User) (authentication.getPrincipal())).getUserId();
        else
            return authentication.getName();
    }

}
