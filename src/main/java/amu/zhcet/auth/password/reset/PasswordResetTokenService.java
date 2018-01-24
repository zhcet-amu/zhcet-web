package amu.zhcet.auth.password.reset;

import amu.zhcet.data.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.UUID;

@Slf4j
@Service
class PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    PasswordResetTokenService(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    /**
     * Sets the token as used so that it may not be reused
     * Also sets all previous tokens as used as well
     * @param passwordResetToken Token to be set as saved
     */
    public void setUsed(PasswordResetToken passwordResetToken) {
        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);

        passwordResetTokenRepository
                .findAllByTokenAndCreatedAtBefore(passwordResetToken.getToken(), passwordResetToken.getCreatedAt())
                .forEach(token -> {
                    token.setUsed(true);
                    passwordResetTokenRepository.save(token);
                });
    }

    public PasswordResetToken findByToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    PasswordResetToken generate(User user) {
        Assert.notNull(user, "User should not be null");

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUser(user);
        passwordResetToken.setToken(UUID.randomUUID().toString());
        passwordResetTokenRepository.save(passwordResetToken);

        return passwordResetToken;
    }
}
