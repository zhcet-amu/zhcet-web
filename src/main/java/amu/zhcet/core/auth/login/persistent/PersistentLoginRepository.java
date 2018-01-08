package amu.zhcet.core.auth.login.persistent;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersistentLoginRepository extends JpaRepository<PersistentLogin, String> {

    void deleteByUsername(String username);

}
