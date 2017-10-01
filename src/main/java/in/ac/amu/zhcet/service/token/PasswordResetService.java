package in.ac.amu.zhcet.service.token;

import in.ac.amu.zhcet.data.model.token.PasswordResetToken;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.data.repository.PasswordResetTokenRepository;
import in.ac.amu.zhcet.service.EmailService;
import in.ac.amu.zhcet.service.core.ConfigurationService;
import in.ac.amu.zhcet.service.core.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class PasswordResetService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final UserService userService;
    private final ConfigurationService configurationService;

    @Autowired
    public PasswordResetService(PasswordResetTokenRepository passwordResetTokenRepository, EmailService emailService, UserService userService, ConfigurationService configurationService) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
        this.userService = userService;
        this.configurationService = configurationService;
    }

    public String validate(String id, String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (passwordResetToken == null || !passwordResetToken.getUserAuth().getUserId().equals(id))
            return "Token: "+ token +" is invalid";

        if (passwordResetToken.isUsed())
            return "Token: "+ token +" is already used! Please generate another reset link!";

        Calendar cal = Calendar.getInstance();
        if ((passwordResetToken.getExpiry().getTime() - cal.getTime().getTime()) <= 0) {
            return "Token: "+token+" for User: "+id+" has expired";
        }
        UserAuth user = passwordResetToken.getUserAuth();
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, Collections.singletonList(new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        return null;
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public PasswordResetToken generate(String email) {
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        UserAuth userAuth = userService.getUserByEmail(email);
        if (userAuth == null)
            throw new UsernameNotFoundException("User with the email " + email + " not found");
        if (!userAuth.isActive())
            throw new UsernameNotFoundException("User with the email " + email + " has not verified its account!");
        passwordResetToken.setUserAuth(userAuth);
        passwordResetToken.setToken(generateToken());
        passwordResetTokenRepository.save(passwordResetToken);
        return passwordResetToken;
    }

    public void sendMail(PasswordResetToken token) {
        String url = configurationService.getBaseUrl() + "/login/reset_password?id=" + token.getUserAuth().getUserId() + "&token=" + token.getToken();

        Map<String, Object> map = new HashMap<>();
        map.put("title", "Password Reset Link");
        map.put("name", token.getUserAuth().getName());
        map.put("link", url);
        map.put("link_text", "Reset Password");
        map.put("pre_message", "You requested password reset on zhcet for user ID: " + token.getUserAuth().getUserId() +
                "<br>Please click the button below to reset your password");
        map.put("post_message", "If you did not request the password reset, please contact website admin");

        log.info("Password reset link generated : " + url);
        String message = emailService.render("html/link", map);

        emailService.sendHtmlMail(token.getUserAuth().getEmail(), "ZHCET Reset Password Link", message);
    }

    public void resetPassword(String newPassword, String token) {
        UserAuth user = (UserAuth) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.changeUserPassword(user, newPassword);

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);
    }
}
