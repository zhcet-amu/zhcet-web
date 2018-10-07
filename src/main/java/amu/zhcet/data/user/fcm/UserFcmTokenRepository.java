package amu.zhcet.data.user.fcm;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;

public interface UserFcmTokenRepository extends CrudRepository<UserFcmToken, String> {

    Optional<UserFcmToken> findByFcmToken(String token);

    Optional<UserFcmToken> findByUser_UserIdAndFcmToken(String userId, String fcmToken);

    Set<UserFcmToken> findAllByUser_UserIdAndDisabledFalse(String userId);

}
