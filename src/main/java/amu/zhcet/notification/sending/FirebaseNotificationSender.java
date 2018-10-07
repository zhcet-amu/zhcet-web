package amu.zhcet.notification.sending;

import amu.zhcet.data.user.fcm.UserFcmToken;
import amu.zhcet.data.user.fcm.UserFcmTokenRepository;
import amu.zhcet.firebase.messaging.FirebaseMessagingService;
import amu.zhcet.notification.Notification;
import amu.zhcet.notification.recipient.NotificationRecipient;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
class FirebaseNotificationSender {

    private final FirebaseMessagingService firebaseMessagingService;
    private final UserFcmTokenRepository userFcmTokenRepository;

    FirebaseNotificationSender(FirebaseMessagingService firebaseMessagingService, UserFcmTokenRepository userFcmTokenRepository) {
        this.firebaseMessagingService = firebaseMessagingService;
        this.userFcmTokenRepository = userFcmTokenRepository;
    }

    public void sendFirebaseNotification(NotificationRecipient notificationRecipient) {
        Set<UserFcmToken> fcmTokens = userFcmTokenRepository.findAllByUser_UserIdAndDisabledFalse(
                notificationRecipient.getRecipient().getUserId()
        );

        if (fcmTokens == null || fcmTokens.isEmpty())
            return;

        fcmTokens.stream()
                .map(UserFcmToken::getFcmToken)
                .forEach(fcmToken ->
                        firebaseMessagingService.sendMessage(
                                createMessage(notificationRecipient.getNotification(), fcmToken),
                                fcmToken
                        )
                );
    }

    private static Message createMessage(Notification notification, String fcmToken) {
        String title = notification.getSender().getName() + " : " + notification.getTitle();
        String message = notification.getMessage();
        String icon = getIcon(notification);

        Map<String, String> data = new HashMap<>();
        data.put("title", notification.getTitle());
        data.put("message", notification.getMessage());
        data.put("sender", notification.getSender().getName());
        data.put("sentTime", notification.getSentTime().toString());
        data.put("icon", getIcon(notification));

        return Message.builder()
                .setNotification(new com.google.firebase.messaging.Notification(title, message))
                .setWebpushConfig(WebpushConfig.builder()
                    .setNotification(new WebpushNotification(title, message, icon))
                    .putAllData(data)
                    .build())
                .putAllData(data)
                .setToken(fcmToken)
                .build();
    }

    private static String getIcon(Notification notification) {
        String icon = notification.getIcon();

        if (icon != null)
            return icon;
        return "https://zhcet-backend.firebaseapp.com/static/img/icon.png";
    }
}
