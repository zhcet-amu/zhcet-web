package amu.zhcet.event;

import amu.zhcet.core.admin.faculty.attendance.upload.AttendanceUploadEvent;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.user.faculty.FacultyMember;
import amu.zhcet.email.LinkMessage;
import amu.zhcet.notification.ChannelType;
import amu.zhcet.notification.Notification;
import amu.zhcet.notification.sending.NotificationSendingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Slf4j
@Component
@Transactional
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
        notification.setLinkMessageConverter(notification1 -> LinkMessage.builder()
                .title(notification1.getTitle())
                .subject(String.format("ZHCET Course %s %s", notification1.getRecipientChannel(), notification1.getTitle()))
                .relativeLink("/dashboard/student/attendance")
                .linkText("View Attendance")
                .preMessage(notification1.getMessage() + "\nPlease click the button below to view your attendance")
                .markdown(true)
                .build());
        notificationSendingService.sendNotification(notification);
    }

}
