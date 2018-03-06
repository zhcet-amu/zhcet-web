package amu.zhcet.event;

import amu.zhcet.core.admin.faculty.attendance.upload.AttendanceUploadEvent;
import amu.zhcet.core.notification.ChannelType;
import amu.zhcet.core.notification.Notification;
import amu.zhcet.core.notification.sending.NotificationSendingService;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.user.faculty.FacultyMember;
import amu.zhcet.email.EmailSendingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AttendanceUploadListener {

    private final NotificationSendingService notificationSendingService;

    public AttendanceUploadListener(NotificationSendingService notificationSendingService) {
        this.notificationSendingService = notificationSendingService;
    }

    @Async
    @EventListener
    public void handleAttendanceUpload(AttendanceUploadEvent attendanceUploadEvent) {
        log.info("Attendance Uploaded by {}. Sending Notification", attendanceUploadEvent.getCourseInCharge());
        sendNotification(attendanceUploadEvent.getCourseInCharge().getFacultyMember(),
                attendanceUploadEvent.getCourseInCharge().getFloatedCourse().getCourse());
    }

    private static Notification fromStudent(FacultyMember sender, Course course) {
        return Notification.builder()
                .automated(true)
                .channelType(ChannelType.TAUGHT_COURSE)
                .recipientChannel(course.getCode())
                .sender(sender.getUser())
                .title("Attendance Update")
                .message(String.format("The attendance for course **%s** - *%s* has just been updated",
                        course.getCode(), course.getTitle()))
                .build();
    }

    private void sendNotification(FacultyMember sender, Course course) {
        Notification notification = fromStudent(sender, course);
        notification.setMeta(EmailSendingService.ATTENDANCE_TYPE);
        notificationSendingService.sendNotification(notification);
    }

}
