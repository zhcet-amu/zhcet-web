package amu.zhcet.firebase.messaging.token;

import amu.zhcet.data.user.fcm.UserFcmTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessagingTokenDetachService {

    private final UserFcmTokenRepository userFcmTokenRepository;

    public MessagingTokenDetachService(UserFcmTokenRepository userFcmTokenRepository) {
        this.userFcmTokenRepository = userFcmTokenRepository;
    }

    public void detachToken(@NonNull String token) {
        userFcmTokenRepository
                .findByFcmToken(token)
                .map(userFcmToken -> {
                    log.warn("Deleting FCM token {} for user {}", token, userFcmToken.getUserId());
                    userFcmToken.setDisabled(true);
                    return userFcmToken;
                }).ifPresent(userFcmTokenRepository::save);
    }
}
