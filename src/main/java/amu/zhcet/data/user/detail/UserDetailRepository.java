package amu.zhcet.data.user.detail;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserDetailRepository extends CrudRepository<UserDetail, String> {

    Optional<UserDetail> findByFcmToken(String token);

}
