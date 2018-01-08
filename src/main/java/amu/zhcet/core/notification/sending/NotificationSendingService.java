package amu.zhcet.core.notification.sending;

import amu.zhcet.core.notification.Notification;
import amu.zhcet.core.notification.recipient.CachedNotificationService;
import amu.zhcet.core.notification.recipient.NotificationRecipient;
import amu.zhcet.data.user.User;
import amu.zhcet.email.EmailSendingService;
import amu.zhcet.firebase.messaging.FirebaseMessagingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
public class NotificationSendingService {

    private final EmailSendingService emailSendingService;
    private final FirebaseMessagingService firebaseMessagingService;
    private final CachedNotificationService cachedNotificationService;
    private final UserExtractor userExtractor;

    @Autowired
    public NotificationSendingService(
            EmailSendingService emailSendingService,
            FirebaseMessagingService firebaseMessagingService,
            CachedNotificationService cachedNotificationService,
            UserExtractor userExtractor
    ) {
        this.emailSendingService = emailSendingService;
        this.firebaseMessagingService = firebaseMessagingService;
        this.cachedNotificationService = cachedNotificationService;
        this.userExtractor = userExtractor;
    }

    private void sendNotificationOnly(Notification notification) {
        if (notification.getSentTime() == null)
            notification.setSentTime(LocalDateTime.now());
        cachedNotificationService.save(notification);
    }

    @Async
    public void sendNotification(Notification notification) {
        sendNotificationOnly(notification);
        sendToRecipients(notification);
    }

    /**
     * Asynchronously send notification to different channel types: Student, Course, Section, Faculty, Department, etc
     * Currently, only student, course, taught course, section and faculty are supported
     * @param notification Notification to be sent, containing message and channel information
     */
    @Async
    public void sendToRecipients(Notification notification) {
        switch (notification.getChannelType()) {
            case STUDENT:
                sendToStudent(notification);
                break;
            case COURSE:
                sendToCourse(notification);
                break;
            case TAUGHT_COURSE:
                sendToTaughtCourse(notification);
            case SECTION:
                sendToSection(notification);
                break;
            case FACULTY:
                sendToFaculty(notification);
                break;
            default:
                // Do nothing
        }
    }

    private void sendToTaughtCourse(Notification notification) {
        userExtractor.fromTaughtCourse(notification.getRecipientChannel(), notification.getSender().getUserId(),
                user -> sendUserNotification(notification, user));
    }

    private void sendToFaculty(Notification notification) {
        userExtractor.fromFacultyId(notification.getRecipientChannel(),
                user -> sendUserNotification(notification, user));
    }

    private void sendToSection(Notification notification) {
        userExtractor.fromSection(notification.getRecipientChannel(),
                user -> sendUserNotification(notification, user));
    }

    private void sendToCourse(Notification notification) {
        userExtractor.fromFloatedCourse(notification.getRecipientChannel(),
                user -> sendUserNotification(notification, user));
    }

    private void sendToStudent(Notification notification) {
        userExtractor.fromStudentId(notification.getRecipientChannel(),
                user -> sendUserNotification(notification, user));
    }

    private void sendUserNotification(Notification notification, User user) {
        NotificationRecipient notificationRecipient = new NotificationRecipient();
        notificationRecipient.setNotification(notification);
        notificationRecipient.setRecipient(user);
        cachedNotificationService.save(notificationRecipient);
        cachedNotificationService.resetUnreadCount(user.getUserId());
        emailSendingService.sendEmailForNotification(notificationRecipient);
        firebaseMessagingService.sendMessage(notificationRecipient);
    }
}
