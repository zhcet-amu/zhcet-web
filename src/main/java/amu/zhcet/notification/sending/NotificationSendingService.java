package amu.zhcet.notification.sending;

import amu.zhcet.common.UserExtractor;
import amu.zhcet.data.user.User;
import amu.zhcet.notification.Notification;
import amu.zhcet.notification.recipient.CachedNotificationService;
import amu.zhcet.notification.recipient.NotificationRecipient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class NotificationSendingService {

    private final EmailNotificationSender emailSendingService;
    private final FirebaseNotificationSender firebaseNotificationSender;
    private final CachedNotificationService cachedNotificationService;
    private final UserExtractor userExtractor;

    @Autowired
    public NotificationSendingService(
            EmailNotificationSender emailSendingService,
            FirebaseNotificationSender firebaseNotificationSender,
            CachedNotificationService cachedNotificationService,
            UserExtractor userExtractor
    ) {
        this.emailSendingService = emailSendingService;
        this.firebaseNotificationSender = firebaseNotificationSender;
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
        // Save the primary notification in server
        sendNotificationOnly(notification);
        // Save the notification for all recipients
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

    // These notifications are bulk notifications and thus emails or firebase notifications for these
    // should be sent in BCC or Topics if possible to reduce the network load. The disadvantage is that
    // these cannot be personalized (and can't contain unsubscribe link) and have to be be sent as
    // same email to everyone

    private void sendToTaughtCourse(Notification notification) {
        sendUserNotifications(notification,
                userExtractor.fromTaughtCourse(
                        notification.getRecipientChannel(),
                        notification.getSender().getUserId())
                        .collect(Collectors.toList()));
    }

    private void sendToSection(Notification notification) {
        sendUserNotifications(notification, userExtractor
                .fromSection(notification.getRecipientChannel())
                .collect(Collectors.toList()));
    }

    private void sendToCourse(Notification notification) {
        sendUserNotifications(notification, userExtractor
                .fromFloatedCourse(notification.getRecipientChannel())
                .collect(Collectors.toList()));
    }

    // Singular notifications can be sent individually and hence can be personalized

    private void sendToFaculty(Notification notification) {
        userExtractor.fromFacultyId(notification.getRecipientChannel())
                .ifPresent(user -> sendUserNotification(notification, user));
    }

    private void sendToStudent(Notification notification) {
        userExtractor.fromStudentId(notification.getRecipientChannel())
                .ifPresent(user -> sendUserNotification(notification, user));
    }

    private void sendUserNotification(Notification notification, User user) {
        NotificationRecipient notificationRecipient = fromNotification(notification, user);
        cachedNotificationService.save(notificationRecipient);
        completeNotificationDistribution(notificationRecipient);
    }

    private void completeNotificationDistribution(NotificationRecipient notificationRecipient) {
        Notification notification = notificationRecipient.getNotification();
        User user = notificationRecipient.getRecipient();
        cachedNotificationService.resetUnreadCount(user.getUserId());
        if (!notification.isStopEmailPropagation())
            emailSendingService.sendEmailForNotification(notificationRecipient);
        if (!notification.isStopFirebasePropagation())
            firebaseNotificationSender.sendFirebaseNotification(notificationRecipient);
    }

    private void sendUserNotifications(Notification notification, List<User> users) {
        if (users.isEmpty())
            return;

        if (!notification.isStopEmailPropagation()) {
            emailSendingService.sendEmailForNotification(notification, users);
            notification.setStopEmailPropagation(true);
        }

        List<NotificationRecipient> notificationRecipients = users.stream()
                .map(user -> fromNotification(notification, user))
                .collect(Collectors.toList());
        cachedNotificationService.saveAll(notificationRecipients);
        notificationRecipients.forEach(this::completeNotificationDistribution);
    }

    private NotificationRecipient fromNotification(Notification notification, User user) {
        NotificationRecipient notificationRecipient = new NotificationRecipient();
        notificationRecipient.setNotification(notification);
        notificationRecipient.setRecipient(user);
        return notificationRecipient;
    }

}
