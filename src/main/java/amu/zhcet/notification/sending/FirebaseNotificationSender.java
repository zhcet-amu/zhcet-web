package amu.zhcet.notification.sending;

import amu.zhcet.common.markdown.MarkDownService;
import amu.zhcet.notification.Notification;
import amu.zhcet.notification.recipient.NotificationRecipient;
import amu.zhcet.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.Message;
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

        firebaseMessagingService.sendMessage(createMessage(notificationRecipient.getNotification(), fcmToken, markDownService::render), fcmToken);
    }

    private static Message createMessage(Notification notification, String fcmToken, Function<String, String> renderer) {
        String title = renderer == null ? notification.getTitle() : renderer.apply(notification.getTitle());
        String message = renderer == null ? notification.getMessage() : renderer.apply(notification.getMessage());

        return Message.builder()
                .setNotification(new com.google.firebase.messaging.Notification("New Notification", notification.getTitle()))
                .putData("title", title)
                .putData("message", message)
                .putData("sender", notification.getSender().getName())
                .putData("sentTime", notification.getSentTime().toString())
                .setToken(fcmToken)
                .build();
    }
}
