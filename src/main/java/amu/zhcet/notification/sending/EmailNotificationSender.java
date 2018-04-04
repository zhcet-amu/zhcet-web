package amu.zhcet.notification.sending;

import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserService;
import amu.zhcet.email.LinkMailService;
import amu.zhcet.email.LinkMessage;
import amu.zhcet.notification.Notification;
import amu.zhcet.notification.recipient.NotificationRecipient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmailNotificationSender {

    private final LinkMailService linkMailService;

    @Autowired
    public EmailNotificationSender(LinkMailService linkMailService) {
        this.linkMailService = linkMailService;
    }

    @Async
    public void sendEmailForNotification(NotificationRecipient notificationRecipient) {
        if (!notificationRecipient.getRecipient().isEmailVerified() || notificationRecipient.getRecipient().isEmailUnsubscribed())
            return;

        LinkMessage payLoad = getPayLoad(notificationRecipient.getNotification());
        payLoad.setRecipientEmail(notificationRecipient.getRecipient().getEmail());
        payLoad.setName(notificationRecipient.getRecipient().getName());

        linkMailService.sendEmail(payLoad);
    }

    public void sendEmailForNotification(Notification notification, List<User> users) {
        List<String> emails = UserService.verifiedUsers(users.stream())
                .map(User::getEmail)
                .collect(Collectors.toList());

        if (emails.isEmpty()) {
            log.debug("No emails found for notification {}", notification);
            return;
        }

        LinkMessage payload = notification.getLinkMessageConverter() == null ?
                getPayLoad(notification) :
                notification.getLinkMessageConverter().apply(notification);
        payload.setBcc(emails);
        linkMailService.sendEmail(payload);
    }

    private LinkMessage getPayLoad(Notification notification) {
        sanitize(notification);
        return LinkMessage.builder()
                .title(notification.getTitle())
                .subject("ZHCET Notification : " + notification.getTitle())
                .relativeLink("/notifications")
                .linkText("View Notifications")
                .preMessage("You got a notification from `" + notification.getSender().getName() + "` : \n\n" +
                        notification.getMessage() + "\n\nPlease click the button below to view notifications")
                .build();
    }

    private void sanitize(Notification notification) {
        String title = notification.getTitle();
        if (title != null)
            notification.setTitle(HtmlUtils.htmlEscape(title));
        String message = notification.getMessage();
        if (message != null)
            notification.setMessage(HtmlUtils.htmlEscape(message));
    }
}
