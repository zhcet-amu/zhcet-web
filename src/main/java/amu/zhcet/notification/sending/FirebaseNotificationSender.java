package amu.zhcet.notification.sending;

import amu.zhcet.firebase.messaging.FirebaseMessagingService;
import amu.zhcet.notification.Notification;
import amu.zhcet.notification.recipient.NotificationRecipient;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

@Service
class FirebaseNotificationSender {

    private final FirebaseMessagingService firebaseMessagingService;

    FirebaseNotificationSender(FirebaseMessagingService firebaseMessagingService) {
        this.firebaseMessagingService = firebaseMessagingService;
    }

    public void sendFirebaseNotification(NotificationRecipient notificationRecipient) {
        String fcmToken = notificationRecipient.getRecipient().getDetails().getFcmToken();
        if (fcmToken == null)
            return;

        firebaseMessagingService.sendMessage(createMessage(notificationRecipient.getNotification(), fcmToken), fcmToken);
    }

    private static Message createMessage(Notification notification, String fcmToken) {
        return Message.builder()
                .setNotification(new com.google.firebase.messaging.Notification(
                        notification.getSender().getName() + " : " + notification.getTitle(),
                        notification.getMessage()))
                .putData("title", notification.getTitle())
                .putData("message", notification.getMessage())
                .putData("sender", notification.getSender().getName())
                .putData("sentTime", notification.getSentTime().toString())
                .setToken(fcmToken)
                .build();
    }
}
