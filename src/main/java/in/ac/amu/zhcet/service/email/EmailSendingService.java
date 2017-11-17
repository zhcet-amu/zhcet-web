package in.ac.amu.zhcet.service.email;

import in.ac.amu.zhcet.data.model.notification.Notification;
import in.ac.amu.zhcet.data.model.notification.NotificationRecipient;
import in.ac.amu.zhcet.service.email.data.LinkMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailSendingService {

    // Meta tags for automated notifications used to identify custom made emails
    public static final String ATTENDANCE_TYPE = "ATTENDANCE_TYPE";

    private final LinkMailService linkMailService;

    @Autowired
    public EmailSendingService(LinkMailService linkMailService) {
        this.linkMailService = linkMailService;
    }

    @Async
    public void sendEmailForNotification(NotificationRecipient notificationRecipient) {
        if (!notificationRecipient.getRecipient().isEmailVerified())
            return;

        LinkMessage payLoad = getPayLoad(notificationRecipient.getNotification());
        payLoad.setRecipientEmail(notificationRecipient.getRecipient().getEmail());
        payLoad.setName(notificationRecipient.getRecipient().getName());

        linkMailService.sendEmail(payLoad);
    }

    private LinkMessage getPayLoadForAttendance(Notification notification) {
        return LinkMessage.builder()
                .title(notification.getTitle())
                .subject(String.format("ZHCET Course %s %s", notification.getRecipientChannel(), notification.getTitle()))
                .relativeLink("/student/attendance")
                .linkText("View Attendance")
                .preMessage(notification.getMessage() + "\nPlease click the button below to view your attendance")
                .markdown(true)
                .build();
    }

    private LinkMessage getPayLoad(Notification notification) {
        if (notification.isAutomated() && notification.getMeta() != null) {
            switch (notification.getMeta()) {
                case ATTENDANCE_TYPE:
                    return getPayLoadForAttendance(notification);
                default:
                    // Do Nothing
            }
        }

        return LinkMessage.builder()
                .title(notification.getTitle())
                .subject("ZHCET Notification : " + notification.getTitle())
                .relativeLink("/notifications")
                .linkText("View Notifications")
                .preMessage("You got a notification from `" + notification.getSender().getName() + "` : \n\n" +
                        notification.getMessage() + "\n\nPlease click the button below to view notifications")
                .markdown(true)
                .build();
    }
}
