package in.ac.amu.zhcet.service.security.password;

import in.ac.amu.zhcet.data.model.user.User;
import in.ac.amu.zhcet.service.UserService;
import in.ac.amu.zhcet.service.user.UserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PasswordChangeService {

    public static int MIN_PASSWORD_LENGTH = 8;

    private final UserService userService;
    private final UserDetailService userDetailService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordChangeService(UserService userService, UserDetailService userDetailService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userDetailService = userDetailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional

    public void resetPassword(@Valid PasswordConfirm passwordConfirm, User user) throws PasswordVerificationException {
        verifyPasswordReset(passwordConfirm, user);
        changeUserPassword(user, passwordConfirm.getNewPassword());
    }

    @Transactional
    public void changePassword(@Valid PasswordChange passwordChange, User user) throws PasswordVerificationException {
        verifyPasswordChange(passwordChange, user);
        changeUserPassword(user, passwordChange.getNewPassword());
    }

    private void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordChanged(true);
        userDetailService.updatePrincipal(user);
        userService.save(user);
    }

    public static List<String> validatePasswordLength(String password, String confirm) {
        List<String> errors = new ArrayList<>();

        if(!password.equals(confirm))
            errors.add("Passwords don't match!");
        if (password.length() < MIN_PASSWORD_LENGTH)
            errors.add("Passwords should be at least " + MIN_PASSWORD_LENGTH + " characters long!");

        return errors;
    }

    private List<String> getPasswordConfirmErrors(PasswordConfirm passwordConfirm, User user) {
        if (passwordConfirm == null || user == null)
            throw new PasswordVerificationException("Password or User is not supplied");

        if (!user.isEmailVerified())
            throw new PasswordVerificationException("Cannot change password for unverified user");

        List<String> verificationErrors = validatePasswordLength(passwordConfirm.getNewPassword(), passwordConfirm.getConfirmPassword());

        if (passwordEncoder.matches(passwordConfirm.getNewPassword(), user.getPassword()))
            throw new PasswordVerificationException("New Password cannot be same as previous password");

        return verificationErrors;
    }

    private void verifyPasswordReset(PasswordConfirm passwordConfirm, User user) throws PasswordVerificationException {
        List<String> verificationErrors = getPasswordConfirmErrors(passwordConfirm, user);
        if (!verificationErrors.isEmpty())
            throw new PasswordVerificationException(verificationErrors);
    }

    private void verifyPasswordChange(PasswordChange passwordChange, User user) throws PasswordVerificationException {
        if (passwordChange == null || user == null)
            throw new PasswordVerificationException("Password or User is not supplied");

        if (!passwordEncoder.matches(passwordChange.getOldPassword(), user.getPassword()))
            throw new PasswordVerificationException("Old Password does not match supplied password");

        List<String> verificationErrors = getPasswordConfirmErrors(passwordChange, user);

        if (!verificationErrors.isEmpty())
            throw new PasswordVerificationException(verificationErrors);
    }

}
