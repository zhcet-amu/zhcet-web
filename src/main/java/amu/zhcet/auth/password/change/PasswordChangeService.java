package amu.zhcet.auth.password.change;

import amu.zhcet.auth.AuthManager;
import amu.zhcet.auth.password.PasswordValidationException;
import amu.zhcet.auth.password.PasswordValidator;
import amu.zhcet.auth.password.PasswordChange;
import amu.zhcet.core.error.ErrorUtils;
import amu.zhcet.data.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;

@Slf4j
@Service
public class PasswordChangeService {

    private final AuthManager authManager;
    private final PasswordValidator passwordValidator;

    @Autowired
    public PasswordChangeService(AuthManager authManager, PasswordValidator passwordValidator) {
        this.authManager = authManager;
        this.passwordValidator = passwordValidator;
    }

    /**
     * Changes password of a user
     * @param user User whose password is to be changed
     * @param passwordChange Password Container
     * @throws PasswordValidationException If password is not of correct form
     */
    @Transactional
    public void changePassword(User user, PasswordChange passwordChange) throws PasswordValidationException {
        ErrorUtils.requireNonNullUser(user);
        Assert.notNull(passwordChange, "PasswordReset should not be null");

        if (!user.isEmailVerified())
            throw new PasswordValidationException("Cannot change password for unverified user");

        // Validate and set the password
        passwordValidator.validateAndSetPasswordChange(user, passwordChange);
        authManager.updatePassword(user);
    }

}
