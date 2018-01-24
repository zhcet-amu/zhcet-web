package amu.zhcet.auth.password;

import amu.zhcet.data.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
public class PasswordValidator {

    private static int MIN_PASSWORD_LENGTH = 8;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    PasswordValidator(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Validates that password and confirm password are equal, of minimum length
     * and new password is not same as old password
     *
     * Encrypts the new password and sets it on user
     * @param user User whose password has to be validated
     * @param passwordConfirm Password Container
     */
    public void validateAndSetPasswordReset(User user, PasswordConfirm passwordConfirm) {
        validatePasswordCommon(user, passwordConfirm);
        validateNewPassword(user.getPassword(), passwordConfirm.getNewPassword());

        // Encrypt password and set
        user.setPassword(passwordEncoder.encode(passwordConfirm.getNewPassword()));
    }

    /**
     * Validates that old password is same as the one provided and,
     * Validates that password and confirm password are equal, of minimum length
     * and new password is not same as old password
     *
     * Encrypts the new password and set it on user
     * @param user User whose password has to be validated
     * @param passwordChange Password Container
     */
    public void validateAndSetPasswordChange(User user, PasswordChange passwordChange) {
        validatePasswordCommon(user, passwordChange);
        validateOldPassword(user.getPassword(), passwordChange.getOldPassword());
        validateNewPassword(user.getPassword(), passwordChange.getNewPassword());

        // Encrypt password and set
        user.setPassword(passwordEncoder.encode(passwordChange.getNewPassword()));
    }

    private void validatePasswordCommon(User user, PasswordConfirm passwordConfirm) {
        Assert.notNull(user, "User should not be null");
        validatePasswordEquality(passwordConfirm.getNewPassword(), passwordConfirm.getConfirmPassword());
        validatePasswordLength(passwordConfirm.getNewPassword());
    }

    public static void validatePasswordEquality(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm))
            throw new PasswordValidationException("Passwords don't match");
    }

    public static void validatePasswordLength(String password) {
        if (password.length() < MIN_PASSWORD_LENGTH)
            throw new PasswordValidationException("Passwords should be at least " + MIN_PASSWORD_LENGTH + " characters long!");
    }

    void validateOldPassword(String oldHashedPassword, String oldRawPassword) {
        log.info(oldHashedPassword);
        log.info(oldRawPassword);
        if (!passwordEncoder.matches(oldRawPassword, oldHashedPassword))
            throw new PasswordValidationException("Old Password does not match supplied password");
    }

    void validateNewPassword(String oldHashedPassword, String newRawPassword) {
        if (passwordEncoder.matches(newRawPassword, oldHashedPassword))
            throw new PasswordValidationException("New Password cannot be same as previous password");
    }
}
