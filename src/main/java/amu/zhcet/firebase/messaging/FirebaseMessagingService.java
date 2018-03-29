package amu.zhcet.firebase.messaging;

import amu.zhcet.common.utils.ConsoleColors;
import amu.zhcet.common.utils.ConsoleHelper;
import amu.zhcet.firebase.FirebaseService;
import amu.zhcet.firebase.messaging.token.MessagingTokenDetachService;
import com.google.api.client.http.HttpResponseException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
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
            if (isTokenExpired(e)) {
                // FCM token is expired, remove it
                messagingTokenDetachService.detachToken(token);
            } else {
                log.error("Sending FCM failed {}", e);
            }
        }
    }

    public static boolean isTokenExpired(Throwable throwable) {
        Throwable current = throwable;
        while (!(current instanceof FirebaseMessagingException) && current != null) {
            current = current.getCause();
        }

        if (current == null)
            return false;

        // We have a FirebaseMessagingException

        while (!(current instanceof HttpResponseException) && current != null) {
            current = current.getCause();
        }

        if (current == null)
            return false;

        // We have a HttpResponseException

        HttpResponseException httpResponseException = (HttpResponseException) current;
        int statusCode = httpResponseException.getStatusCode();
        return statusCode == 404 || statusCode == 400;
    }

}
