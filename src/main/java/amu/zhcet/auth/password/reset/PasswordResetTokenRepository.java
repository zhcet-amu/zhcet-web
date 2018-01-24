package amu.zhcet.auth.password.reset;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long>{
    PasswordResetToken findByToken(String token);

    List<PasswordResetToken> findAllByTokenAndCreatedAtBefore(String token, LocalDateTime localDateTime);
}
