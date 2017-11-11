package in.ac.amu.zhcet.service.notification;

import in.ac.amu.zhcet.data.model.notification.Notification;
import in.ac.amu.zhcet.data.model.notification.NotificationRecipient;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.data.repository.NotificationRecipientRepository;
import in.ac.amu.zhcet.data.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
public class NotificationSendingService {

    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;
    private final CachedNotificationService cachedNotificationService;
    private final UserExtractor userExtractor;

    @Autowired
    public NotificationSendingService(
            NotificationRepository notificationRepository,
            NotificationRecipientRepository notificationRecipientRepository,
            CachedNotificationService cachedNotificationService,
            UserExtractor userExtractor
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationRecipientRepository = notificationRecipientRepository;
        this.cachedNotificationService = cachedNotificationService;
        this.userExtractor = userExtractor;
    }

    @Async
    public void sendNotification(Notification notification) {
        notificationRepository.save(notification);
        sendToRecipients(notification);
    }

    /**
     * Asynchronously send notification to different channel types: Student, Course, Section, Faculty, Department, etc
     * Currently, only student course, section and faculty are supported
     * @param notification Notification to be sent, containing message and channel information
     */
    private void sendToRecipients(Notification notification) {
        switch (notification.getChannelType()) {
            case STUDENT:
                sendToStudent(notification);
                break;
            case COURSE:
                sendToCourse(notification);
                break;
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

    private void sendToFaculty(Notification notification) {
        userExtractor.fromFacultyId(notification.getRecipientChannel(),
                userAuth -> saveUserNotification(notification, userAuth));
    }

    private void sendToSection(Notification notification) {
        userExtractor.fromSection(notification.getRecipientChannel(),
                userAuth -> saveUserNotification(notification, userAuth));
    }

    private void sendToCourse(Notification notification) {
        userExtractor.fromFloatedCourse(notification.getRecipientChannel(),
                userAuth -> saveUserNotification(notification, userAuth));
    }

    private void sendToStudent(Notification notification) {
        userExtractor.fromStudentId(notification.getRecipientChannel(),
                userAuth -> saveUserNotification(notification, userAuth));
    }

    private void saveUserNotification(Notification notification, UserAuth userAuth) {
        NotificationRecipient notificationRecipient = new NotificationRecipient();
        notificationRecipient.setNotification(notification);
        notificationRecipient.setRecipient(userAuth);
        notificationRecipientRepository.save(notificationRecipient);
        cachedNotificationService.resetUnreadCount(userAuth.getUserId());
    }
}
