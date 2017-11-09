package in.ac.amu.zhcet.service.notification;

import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.notification.Notification;
import in.ac.amu.zhcet.data.model.notification.NotificationRecipient;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.data.repository.NotificationRecipientRepository;
import in.ac.amu.zhcet.data.repository.NotificationRepository;
import in.ac.amu.zhcet.service.CourseManagementService;
import in.ac.amu.zhcet.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
@Transactional
public class NotificationSendingService {

    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;
    private final CourseManagementService courseManagementService;
    private final StudentService studentService;

    @Autowired
    public NotificationSendingService(NotificationRepository notificationRepository, NotificationRecipientRepository notificationRecipientRepository, CourseManagementService courseManagementService, StudentService studentService) {
        this.notificationRepository = notificationRepository;
        this.notificationRecipientRepository = notificationRecipientRepository;
        this.courseManagementService = courseManagementService;
        this.studentService = studentService;
    }

    @Async
    public void sendNotification(Notification notification) {
        notificationRepository.save(notification);
        sendToRecipients(notification);
    }

    /**
     * Asynchronously send notification to different channel types: Student, Course, Department, etc
     * Currently, only student and course are supported
     * @param notification Notification to be sent, containing message and channel information
     */
    private void sendToRecipients(Notification notification) {
        switch (notification.getChannelType()) {
            case STUDENT:
                sendToStudent(notification);
                break;
            case COURSE:
                sendToCourse(notification);
            default:
                // Do nothing
        }
    }

    private void sendToCourse(Notification notification) {
        String floatedCourseId = notification.getRecipientChannel();
        FloatedCourse floatedCourse = courseManagementService.getFloatedCourseByCode(floatedCourseId);

        if (floatedCourse == null) {
            log.warn("No such floated course exists {}", floatedCourseId);
            return;
        }

        List<CourseRegistration> courseRegistrations = floatedCourse.getCourseRegistrations();
        for (CourseRegistration courseRegistration : courseRegistrations)
            saveUserNotification(notification, courseRegistration.getStudent().getUser());
    }

    private void sendToStudent(Notification notification) {
        String studentId = notification.getRecipientChannel();
        Student recipient = studentService.getByEnrolmentNumber(studentId);
        if (recipient == null)
            recipient = studentService.getByFacultyNumber(studentId);

        if (recipient == null) {
            log.warn("No student found with ID {}", studentId);
            return;
        }

        saveUserNotification(notification, recipient.getUser());
    }

    private void saveUserNotification(Notification notification, UserAuth userAuth) {
        NotificationRecipient notificationRecipient = new NotificationRecipient();
        notificationRecipient.setNotification(notification);
        notificationRecipient.setRecipient(userAuth);
        notificationRecipientRepository.save(notificationRecipient);
    }
}
