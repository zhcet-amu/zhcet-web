package amu.zhcet.core.notification.sending;

import amu.zhcet.common.markdown.MarkDownService;
import amu.zhcet.core.notification.Notification;
import amu.zhcet.core.notification.recipient.NotificationRecipient;
import amu.zhcet.firebase.messaging.FirebaseMessagingService;
import amu.zhcet.firebase.messaging.model.SendRequest;
import amu.zhcet.firebase.messaging.model.request.*;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
class FirebaseNotificationSender {

    private final FirebaseMessagingService firebaseMessagingService;
    private final MarkDownService markDownService;

    FirebaseNotificationSender(FirebaseMessagingService firebaseMessagingService, MarkDownService markDownService) {
        this.firebaseMessagingService = firebaseMessagingService;
        this.markDownService = markDownService;
    }

    public void sendFirebaseNotification(NotificationRecipient notificationRecipient) {
        String fcmToken = notificationRecipient.getRecipient().getDetails().getFcmToken();
        if (fcmToken == null)
            return;

        Notification notification = notificationRecipient.getNotification();

        firebaseMessagingService.sendMessage(FirebaseNotificationSender.createRequest(notification, fcmToken, markDownService::render));
    }

    private static SendRequest createRequest(Notification notification, String fcmToken, Function<String, String> renderer) {
        NotificationBody notificationBody = NotificationBody.builder()
                .title("New Notification")
                .body(notification.getTitle())
                .build();

        WebPushNotification webPushNotification = WebPushNotification.builder()
                .title("New Notification")
                .body(notification.getTitle())
                .build();

        WebPushConfig webPushConfig = WebPushConfig.builder()
                .notification(webPushNotification)
                .build();

        return SendRequest.builder()
                .message(Message.builder()
                        .token(fcmToken)
                        .notification(notificationBody)
                        .webpush(webPushConfig)
                        .data(getDataBody(notification, renderer))
                        .build()
                ).build();
    }

    private static DataBody getDataBody(Notification notification, Function<String, String> renderer) {
        String title = renderer == null ? notification.getTitle() : renderer.apply(notification.getTitle());
        String message = renderer == null ? notification.getMessage() : renderer.apply(notification.getMessage());

        return DataBody.builder()
                .title(title)
                .message(message)
                .sender(notification.getSender().getName())
                .sentTime(notification.getSentTime().toString())
                .build();
    }

}
