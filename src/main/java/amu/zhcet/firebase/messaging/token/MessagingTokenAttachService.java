package amu.zhcet.firebase.messaging.token;

import amu.zhcet.data.user.fcm.UserFcmToken;
import amu.zhcet.data.user.fcm.UserFcmTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
class MessagingTokenAttachService {

    private final UserFcmTokenRepository userFcmTokenRepository;

    @Autowired
    public MessagingTokenAttachService(UserFcmTokenRepository userFcmTokenRepository) {
        this.userFcmTokenRepository = userFcmTokenRepository;
    }

    /**
     * Attaches FCM registration received from front end to user for push notifications
     * @param userId User ID to attach token info to
     * @param token FCM Registration Token to be attached
     */
    public void attachToken(String userId, String token) {
        if (userId == null || token == null)
            return;

        Optional<UserFcmToken> fcmTokenOptional = userFcmTokenRepository.findByUser_UserIdAndFcmToken(userId, token);

        if (fcmTokenOptional.isPresent()) {
            UserFcmToken fcmToken = fcmTokenOptional.get();

            if (!fcmToken.isDisabled()) {
                log.debug("FCM token {} unchanged for user {}", token, userId);
            } else {
                fcmToken.setDisabled(false);
                userFcmTokenRepository.save(fcmToken);
                log.info("ENABLED FCM token {} to user : {}", token, userId);
            }
        } else {
            userFcmTokenRepository.save(new UserFcmToken(userId, token));
            log.info("Added FCM token {} to user : {}", token, userId);
        }
    }

}
