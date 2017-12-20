package in.ac.amu.zhcet.service.security.password;

import in.ac.amu.zhcet.data.model.token.PasswordResetToken;
import in.ac.amu.zhcet.data.model.user.User;
import in.ac.amu.zhcet.data.repository.PasswordResetTokenRepository;
import in.ac.amu.zhcet.service.UserService;
import in.ac.amu.zhcet.service.email.LinkMailService;
import in.ac.amu.zhcet.service.email.data.LinkMessage;
import in.ac.amu.zhcet.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserService userService;
    private final PasswordChangeService passwordChangeService;
    private final LinkMailService linkMailService;

    @Autowired
    public PasswordResetService(
            PasswordResetTokenRepository passwordResetTokenRepository,
            UserService userService,
            PasswordChangeService passwordChangeService,
            LinkMailService linkMailService
    ) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userService = userService;
        this.passwordChangeService = passwordChangeService;
        this.linkMailService = linkMailService;
    }

    public void resetPassword(@Valid PasswordReset passwordReset) throws TokenValidationException, PasswordVerificationException {
        validate(passwordReset.getHash(), passwordReset.getToken(), false);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        passwordChangeService.resetPassword(passwordReset, user);

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(passwordReset.getToken());
        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    private void validate(String hash, String token, boolean setAuth) throws TokenValidationException {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (passwordResetToken == null || !SecurityUtils.hashMatches(passwordResetToken.getUser().getUserId(), hash))
            throw new TokenValidationException("Token: " + token + " is invalid");

        if (passwordResetToken.isUsed())
            throw new TokenValidationException("Token: " + token + " is already used! Please generate another reset link!");

        Calendar cal = Calendar.getInstance();
        if ((passwordResetToken.getExpiry().getTime() - cal.getTime().getTime()) <= 0) {
            throw new TokenValidationException("Token: " + token+" for User: " + passwordResetToken.getUser().getUserId() + " has expired");
        }

        if (setAuth) {
            User user = passwordResetToken.getUser();
            Authentication auth = new UsernamePasswordAuthenticationToken(user, null, Collections.singletonList(new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }

    public void validate(String hash, String token) throws TokenValidationException {
        validate(hash, token, true);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public PasswordResetToken generate(String email) {
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        User user = userService.getUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with the email " + email + " not found"));
        if (!user.isEmailVerified())
            throw new UsernameNotFoundException("User with the email " + email + " has not verified its account!");
        passwordResetToken.setUser(user);
        passwordResetToken.setToken(generateToken());
        passwordResetTokenRepository.save(passwordResetToken);
        return passwordResetToken;
    }

    public void sendMail(PasswordResetToken token) {
        User user = token.getUser();
        String relativeUrl = String.format("/login/password/reset?hash=%s&auth=%s", SecurityUtils.getHash(user.getUserId()), token.getToken());
        log.info("Password reset link generated : {}", relativeUrl);

        LinkMessage linkMessage = getPayLoad(user, relativeUrl);
        linkMailService.sendEmail(linkMessage, false);
    }

    private LinkMessage getPayLoad(User user, String url) {
        return LinkMessage.builder()
                .recipientEmail(user.getEmail())
                .name(user.getName())
                .subject("ZHCET Reset Password Link")
                .title("Password Reset Link")
                .relativeLink(url)
                .linkText("Reset Password")
                .preMessage("You requested password reset on zhcet for user ID: " + user.getUserId() +
                        "<br>Please click the button below to reset your password")
                .postMessage("If you did not request the password reset, please contact website admin")
                .build();
    }
}
