package amu.zhcet.firebase.messaging;

import amu.zhcet.common.utils.ConsoleColors;
import amu.zhcet.common.utils.ConsoleHelper;
import amu.zhcet.firebase.FirebaseService;
import amu.zhcet.firebase.messaging.model.SendRequest;
import amu.zhcet.firebase.messaging.model.SendResponse;
import feign.Feign;
import feign.FeignException;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FirebaseMessagingService {

    private final FirebaseService firebaseService;

    private MessagingClient messagingClient;

    @Autowired
    public FirebaseMessagingService(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;

        boolean canSendMessage = firebaseService.canProceed();
        String color = canSendMessage ? ConsoleColors.GREEN : ConsoleColors.RED;
        log.info(ConsoleHelper.color(color, "CONFIG (Firebase): Firebase Messaging Running : {}"), canSendMessage);
    }

    /**
     * Sends a push notification to notification recipient user using FCM token
     * NotificationBody and DataBody is created for FCM HTTP V1 API using the SendRequest content passed
     * If FCM token of user is not found, sending is skipped
     * @param sendRequest FCM request
     */
    @Async
    public void sendMessage(SendRequest sendRequest) {
        if (!firebaseService.canProceed())
            return;

        try {
            SendResponse sendResponse = getMessagingClient().sendMessage(firebaseService.getToken(), sendRequest);

            log.info("Sent Broadcast : {}", sendResponse);
        } catch (FeignException feignException) {
            log.error("Sending FCM failed", feignException);
        }
    }

    private MessagingClient getMessagingClient() {
        if (messagingClient == null) {
            messagingClient = Feign.builder()
                    .client(new OkHttpClient())
                    .decoder(new JacksonDecoder())
                    .encoder(new JacksonEncoder())
                    .target(MessagingClient.class, firebaseService.getMessagingServer());
        }

        return messagingClient;
    }

}
