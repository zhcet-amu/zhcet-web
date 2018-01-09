package amu.zhcet.firebase.messaging.token;

import amu.zhcet.data.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
class MessagingTokenAttachService {

    private final UserService userService;

    @Autowired
    public MessagingTokenAttachService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Attaches FCM registration received from front end to user for push notifications
     * @param userId User ID to attach token info to
     * @param token FCM Registration Token to be attached
     */
    @Async
    public void attachToken(String userId, String token) {
        if (userId == null || token == null)
            return;

        userService.findById(userId).ifPresent(user -> {
            user.getDetails().setFcmToken(token);
            log.info("Added FCM token {} to user : {}", token, user.getUserId());
            userService.save(user);
        });
    }

}
