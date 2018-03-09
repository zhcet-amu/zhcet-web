package amu.zhcet.event;

import amu.zhcet.notification.ChannelType;
import amu.zhcet.notification.Notification;
import amu.zhcet.notification.sending.NotificationSendingService;
import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserType;
import amu.zhcet.auth.verification.DuplicateEmailEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class DuplicateEmailListener {

    private final NotificationSendingService notificationSendingService;

    public DuplicateEmailListener(NotificationSendingService notificationSendingService) {
        this.notificationSendingService = notificationSendingService;
    }

    @Async
    @EventListener
    public void handleDuplicateEmail(DuplicateEmailEvent duplicateEmailEvent) {
        sendEmailChangeNotification(duplicateEmailEvent.getDuplicateUser(), duplicateEmailEvent.getUser(), duplicateEmailEvent.getEmail());
    }

    private void sendEmailChangeNotification(User recipient, User claimant, String email) {
        Notification notification = Notification.builder()
                .automated(true)
                .channelType(fromUser(recipient))
                .recipientChannel(recipient.getUserId())
                .sender(claimant)
                .title("Email Claimed")
                .message(String.format("Your previously set email **%s** is now claimed by `%s` *(%s)*.\n" +
                        "Please change it or claim it back by verifying it", email, claimant.getName(), claimant.getUserId()))
                .build();

        notificationSendingService.sendNotification(notification);
    }

    private ChannelType fromUser(User user) {
        return user.getType() == UserType.STUDENT ? ChannelType.STUDENT : ChannelType.FACULTY;
    }

}
