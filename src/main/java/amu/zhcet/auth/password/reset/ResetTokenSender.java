package amu.zhcet.auth.password.reset;

import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserService;
import amu.zhcet.email.LinkMailService;
import amu.zhcet.email.LinkMessage;
import amu.zhcet.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
class ResetTokenSender {

    private final PasswordResetTokenService passwordResetTokenService;
    private final UserService userService;
    private final LinkMailService linkMailService;

    @Autowired
    ResetTokenSender(PasswordResetTokenService passwordResetTokenService, UserService userService, LinkMailService linkMailService) {
        this.passwordResetTokenService = passwordResetTokenService;
        this.userService = userService;
        this.linkMailService = linkMailService;
    }

    public void sendResetToken(String email) {
        User user = userService.getUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with the email " + email + " not found"));
        if (!user.isEmailVerified())
            throw new UsernameNotFoundException("User with the email " + email + " has not verified its account!");

        PasswordResetToken passwordResetToken = passwordResetTokenService.generate(user);
        sendMail(passwordResetToken);
    }

    private void sendMail(PasswordResetToken token) {
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
