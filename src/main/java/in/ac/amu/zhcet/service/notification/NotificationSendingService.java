package in.ac.amu.zhcet.service.notification;

import in.ac.amu.zhcet.data.model.notification.Notification;
import in.ac.amu.zhcet.data.model.notification.NotificationRecipient;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.service.firebase.messaging.FirebaseMessagingService;
import in.ac.amu.zhcet.service.email.EmailSendingService;
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
                userAuth -> sendUserNotification(notification, userAuth));
    }

    private void sendToFaculty(Notification notification) {
        userExtractor.fromFacultyId(notification.getRecipientChannel(),
                userAuth -> sendUserNotification(notification, userAuth));
    }

    private void sendToSection(Notification notification) {
        userExtractor.fromSection(notification.getRecipientChannel(),
                userAuth -> sendUserNotification(notification, userAuth));
    }

    private void sendToCourse(Notification notification) {
        userExtractor.fromFloatedCourse(notification.getRecipientChannel(),
                userAuth -> sendUserNotification(notification, userAuth));
    }

    private void sendToStudent(Notification notification) {
        userExtractor.fromStudentId(notification.getRecipientChannel(),
                userAuth -> sendUserNotification(notification, userAuth));
    }

    private void sendUserNotification(Notification notification, UserAuth userAuth) {
        NotificationRecipient notificationRecipient = new NotificationRecipient();
        notificationRecipient.setNotification(notification);
        notificationRecipient.setRecipient(userAuth);
        cachedNotificationService.save(notificationRecipient);
        cachedNotificationService.resetUnreadCount(userAuth.getUserId());
        emailSendingService.sendEmailForNotification(notificationRecipient);
        firebaseMessagingService.sendMessage(notificationRecipient);
    }
}
