package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.token.PersistentLogin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersistentLoginRepository extends JpaRepository<PersistentLogin, String> {

    void deleteByUsername(String username);

}
