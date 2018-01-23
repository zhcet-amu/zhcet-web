package amu.zhcet.firebase.messaging;

import amu.zhcet.common.markdown.MarkDownService;
import amu.zhcet.common.utils.ConsoleColors;
import amu.zhcet.common.utils.ConsoleHelper;
import amu.zhcet.core.notification.Notification;
import amu.zhcet.core.notification.recipient.NotificationRecipient;
import amu.zhcet.firebase.FirebaseService;
import amu.zhcet.firebase.messaging.model.SendRequest;
import amu.zhcet.firebase.messaging.model.SendResponse;
import feign.Feign;
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
    private final MarkDownService markDownService;

    private MessagingClient messagingClient;

    @Autowired
    public FirebaseMessagingService(FirebaseService firebaseService, MarkDownService markDownService) {
        this.firebaseService = firebaseService;
        this.markDownService = markDownService;

        boolean canSendMessage = firebaseService.canProceed();
        String color = canSendMessage ? ConsoleColors.GREEN : ConsoleColors.RED;
        log.info(ConsoleHelper.color(color, "CONFIG (Firebase): Firebase Messaging Running : {}"), canSendMessage);
    }

    /**
     * Sends a push notification to notification recipient user using FCM token
     * NotificationBody and DataBody is created for FCM HTTP V1 API using the Notification content passed
     * If FCM token of user is not found, sending is skipped
     * @param notificationRecipient Notification Recipient
     */
    @Async
    public void sendMessage(NotificationRecipient notificationRecipient) {
        if (!firebaseService.canProceed())
            return;

        String fcmToken = notificationRecipient.getRecipient().getDetails().getFcmToken();

        if (fcmToken == null)
            return;

        Notification notification = notificationRecipient.getNotification();

        SendRequest request = RequestMapper.createRequest(notification, fcmToken, markDownService::render);
        SendResponse sendResponse = getMessagingClient().sendMessage(firebaseService.getToken(), request);

        log.info("Sent Broadcast : {}", sendResponse);
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
