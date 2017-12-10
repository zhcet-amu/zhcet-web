package in.ac.amu.zhcet.service.user.auth;

import in.ac.amu.zhcet.data.model.token.PasswordResetToken;
import in.ac.amu.zhcet.data.model.user.User;
import in.ac.amu.zhcet.data.repository.PasswordResetTokenRepository;
import in.ac.amu.zhcet.service.UserService;
import in.ac.amu.zhcet.service.email.LinkMailService;
import in.ac.amu.zhcet.service.email.data.LinkMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
public class PasswordResetService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserService userService;
    private final LinkMailService linkMailService;

    @Autowired
    public PasswordResetService(PasswordResetTokenRepository passwordResetTokenRepository, UserService userService, LinkMailService linkMailService) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userService = userService;
        this.linkMailService = linkMailService;
    }

    public String validate(String id, String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (passwordResetToken == null || !passwordResetToken.getUser().getUserId().equals(id))
            return "Token: " + token + " is invalid";

        if (passwordResetToken.isUsed())
            return "Token: " + token + " is already used! Please generate another reset link!";

        Calendar cal = Calendar.getInstance();
        if ((passwordResetToken.getExpiry().getTime() - cal.getTime().getTime()) <= 0) {
            return "Token: " + token+" for User: " + id + " has expired";
        }
        User user = passwordResetToken.getUser();
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, Collections.singletonList(new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        return null;
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
        String relativeUrl = String.format("/login/password/reset?id=%s&auth=%s", user.getUserId(), token.getToken());
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

    public void resetPassword(String newPassword, String token) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.changeUserPassword(user, newPassword);

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);
    }
}
