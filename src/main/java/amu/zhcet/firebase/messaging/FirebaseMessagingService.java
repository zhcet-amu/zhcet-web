package amu.zhcet.firebase.messaging;

import amu.zhcet.common.utils.ConsoleColors;
import amu.zhcet.common.utils.ConsoleHelper;
import amu.zhcet.firebase.FirebaseService;
import amu.zhcet.firebase.messaging.token.MessagingTokenDetachService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class FirebaseMessagingService {

    private final FirebaseService firebaseService;

    private final FirebaseMessaging firebaseMessaging;
    private final MessagingTokenDetachService messagingTokenDetachService;

    @Autowired
    public FirebaseMessagingService(FirebaseService firebaseService, MessagingTokenDetachService messagingTokenDetachService) {
        this.firebaseService = firebaseService;
        this.messagingTokenDetachService = messagingTokenDetachService;

        boolean canSendMessage = firebaseService.canProceed();
        String color = canSendMessage ? ConsoleColors.GREEN : ConsoleColors.RED;
        log.info(ConsoleHelper.color(color, "CONFIG (Firebase): Firebase Messaging Running : {}"), canSendMessage);

        if (canSendMessage) {
            firebaseMessaging = FirebaseMessaging.getInstance();
        } else {
            firebaseMessaging = null;
        }
    }

    /**
     * Sends a push notification to notification recipient user using FCM token
     * If FCM token of user is not found, sending is skipped
     *
     * If a token is expired or invalid, it is disabled
     * @param message FCM Message
     */
    @Async
    public void sendMessage(Message message, String token) {
        if (!firebaseService.canProceed())
            return;

        try {
            String response = firebaseMessaging.sendAsync(message).get();
            log.info("Sent Broadcast : {}", response);
        } catch (InterruptedException e) {
            log.error("Sending FCM failed {}", e);
        } catch (ExecutionException e) {
            try {
                FirebaseErrorParsingUtils.TokenStatus tokenStatus = FirebaseErrorParsingUtils.getTokenStatus(e);

                if (!tokenStatus.isValid()) {
                    // FCM token is expired, remove it
                    messagingTokenDetachService.detachToken(token, tokenStatus.getReason());
                } else {
                    log.error("FCM sending failed", e);
                }

            } catch (FirebaseErrorParsingUtils.InvalidThrowableException ite) {
                log.error("Got invalid exception while sending FCM", ite);
            }
        }
    }

}
