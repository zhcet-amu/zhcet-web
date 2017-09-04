package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.token.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long>{
    PasswordResetToken findByToken(String token);
}
