package amu.zhcet.auth.password.reset;

import amu.zhcet.auth.AuthManager;
import amu.zhcet.auth.password.PasswordValidationException;
import amu.zhcet.auth.password.PasswordValidator;
import amu.zhcet.auth.password.PasswordReset;
import amu.zhcet.core.error.ErrorUtils;
import amu.zhcet.data.user.User;
import amu.zhcet.security.CryptoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;

@Slf4j
@Service
class PasswordResetService {

    private final AuthManager authManager;
    private final PasswordValidator passwordValidator;
    private final PasswordResetTokenService resetTokenService;

    @Autowired
    public PasswordResetService(AuthManager authManager, PasswordValidator passwordValidator, PasswordResetTokenService resetTokenService) {
        this.authManager = authManager;
        this.passwordValidator = passwordValidator;
        this.resetTokenService = resetTokenService;
    }

    /**
     * Resets a password of a user who has forgotten it
     * @param user User whose password is to be changed
     * @param passwordReset Password Container with passwords for checking
     * @throws TokenValidationException If the token is expired or invalid
     * @throws PasswordValidationException If the passwords are in invalid format
     */
    public void resetPassword(User user, PasswordReset passwordReset) throws TokenValidationException, PasswordValidationException {
        ErrorUtils.requireNonNullUser(user);
        Assert.notNull(passwordReset, "PasswordReset should not be null");

        PasswordResetToken passwordResetToken = getAndValidate(passwordReset.getHash(), passwordReset.getToken());

        // Verify and set the password
        passwordValidator.validateAndSetPasswordReset(user, passwordReset);
        authManager.resetPassword(user);

        // Set that the token is used so that it may not be reused
        resetTokenService.setUsed(passwordResetToken);
    }

    /**
     * Grants access to an unauthenticated user to temporarily access password change page
     * @param hash Hash of the token sent with Password Reset Email
     * @param token Token sent with Password Reset Email
     */
    public void grantAccess(String hash, String token) {
        PasswordResetToken passwordResetToken = getAndValidate(hash, token);
        authManager.grantChangePasswordPrivilege(passwordResetToken.getUser());
    }

    private PasswordResetToken getAndValidate(String hash, String token) throws TokenValidationException {
        PasswordResetToken passwordResetToken = resetTokenService.findByToken(token);

        if (passwordResetToken == null || !CryptoUtils.hashMatches(passwordResetToken.getUser().getUserId(), hash))
            throw new TokenValidationException("Token: " + token + " is invalid");

        if (passwordResetToken.isUsed())
            throw new TokenValidationException("Token: " + token + " is already used! Please generate another reset link!");

        Calendar cal = Calendar.getInstance();
        if ((passwordResetToken.getExpiry().getTime() - cal.getTime().getTime()) <= 0) {
            throw new TokenValidationException("Token: " + token+" for User: " + passwordResetToken.getUser().getUserId() + " has expired");
        }

        return passwordResetToken;
    }

}
