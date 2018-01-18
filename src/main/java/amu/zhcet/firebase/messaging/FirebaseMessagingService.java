package amu.zhcet.firebase.messaging;

import amu.zhcet.common.markdown.MarkDownService;
import amu.zhcet.common.utils.ConsoleColors;
import amu.zhcet.common.utils.ConsoleHelper;
import amu.zhcet.core.notification.Notification;
import amu.zhcet.core.notification.recipient.NotificationRecipient;
import amu.zhcet.firebase.FirebaseService;
import amu.zhcet.firebase.messaging.model.SendRequest;
import amu.zhcet.firebase.messaging.model.SendResponse;
import amu.zhcet.firebase.messaging.model.request.DataBody;
import amu.zhcet.firebase.messaging.model.request.NotificationBody;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FirebaseMessagingService {

    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final Map<String, Object> HEADER_MAP = new HashMap<>();

    private final FirebaseService firebaseService;
    private final MarkDownService markDownService;

    private MessagingClient messagingClient;

    @Autowired
    public FirebaseMessagingService(FirebaseService firebaseService, MarkDownService markDownService) {
        this.firebaseService = firebaseService;
        this.markDownService = markDownService;

        HEADER_MAP.put("Authorization", "key=" + firebaseService.getMessagingServerKey());
        boolean canSendMessage = firebaseService.canSendMessage();
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
        if (!firebaseService.canSendMessage())
            return;

        String fcmToken = notificationRecipient.getRecipient().getDetails().getFcmToken();

        if (fcmToken == null)
            return;

        Notification notification = notificationRecipient.getNotification();

        NotificationBody notificationBody = NotificationBody.builder()
                .title("New Notification")
                .body(notification.getTitle())
                .build();

        DataBody dataBody = DataBody.builder()
                .title(markDownService.render(notification.getTitle()))
                .message(markDownService.render(notification.getMessage()))
                .sender(notification.getSender().getName())
                .sentTime(notification.getSentTime().toString())
                .build();

        SendResponse sendResponse = getMessagingClient().sendMessage(
                SendRequest.builder()
                        .to(fcmToken)
                        .notification(notificationBody)
                        .data(dataBody)
                        .build(),
                HEADER_MAP);

        log.info("Sent Broadcast : {}", sendResponse);
    }

    private MessagingClient getMessagingClient() {
        if (messagingClient == null) {
            messagingClient = Feign.builder()
                    .client(new OkHttpClient())
                    .decoder(new JacksonDecoder())
                    .encoder(new JacksonEncoder())
                    .target(MessagingClient.class, BASE_URL);
        }

        return messagingClient;
    }

}
