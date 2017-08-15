package in.ac.amu.zhcet.data.service;

import in.ac.amu.zhcet.data.model.PasswordResetToken;
import in.ac.amu.zhcet.data.model.base.user.UserAuth;
import in.ac.amu.zhcet.data.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PasswordResetService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserService userService;

    @Autowired
    public PasswordResetService(PasswordResetTokenRepository passwordResetTokenRepository, UserService userService) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userService = userService;
    }

    public PasswordResetToken getByUserId(String username) {
        return passwordResetTokenRepository.findByUserAuth_UserId(username);
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
        passwordResetToken.setUserAuth(userAuth);
        passwordResetToken.setToken(generateToken());
        passwordResetTokenRepository.save(passwordResetToken);
        return passwordResetToken;
    }

    public void resetPassword(String newPassword, String token) {
        UserAuth user = (UserAuth) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.changeUserPassword(user, newPassword);

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);
    }
}
