package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.token.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
}
