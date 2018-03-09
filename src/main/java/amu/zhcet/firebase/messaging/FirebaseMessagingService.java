package amu.zhcet.firebase.messaging;

import amu.zhcet.common.utils.ConsoleColors;
import amu.zhcet.common.utils.ConsoleHelper;
import amu.zhcet.firebase.FirebaseService;
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

    @Autowired
    public FirebaseMessagingService(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;

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
     * @param message FCM Message
     */
    @Async
    public void sendMessage(Message message) {
        if (!firebaseService.canProceed())
            return;

        try {
            String response = firebaseMessaging.sendAsync(message).get();
            log.info("Sent Broadcast : {}", response);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Sending FCM failed {}", e);
        }
    }

}
