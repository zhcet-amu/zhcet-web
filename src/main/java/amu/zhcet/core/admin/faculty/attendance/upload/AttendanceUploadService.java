package amu.zhcet.core.admin.faculty.attendance.upload;

import amu.zhcet.core.notification.ChannelType;
import amu.zhcet.core.notification.Notification;
import amu.zhcet.core.notification.sending.NotificationSendingService;
import amu.zhcet.data.attendance.AttendanceUpload;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.incharge.CourseInCharge;
import amu.zhcet.data.course.incharge.CourseInChargeService;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.data.course.registration.CourseRegistrationService;
import amu.zhcet.data.user.faculty.FacultyMember;
import amu.zhcet.email.EmailSendingService;
import amu.zhcet.storage.csv.Confirmation;
import amu.zhcet.storage.csv.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
class AttendanceUploadService {

    private final CourseInChargeService courseInChargeService;
    private final CourseRegistrationService courseRegistrationService;
    private final NotificationSendingService notificationSendingService;
    private final AttendanceUploadAdapter attendanceUploadAdapter;

    @Autowired
    public AttendanceUploadService(
            CourseInChargeService courseInChargeService,
            CourseRegistrationService courseRegistrationService,
            NotificationSendingService notificationSendingService,
            AttendanceUploadAdapter attendanceUploadAdapter
    ) {
        this.attendanceUploadAdapter = attendanceUploadAdapter;
        this.courseInChargeService = courseInChargeService;
        this.courseRegistrationService = courseRegistrationService;
        this.notificationSendingService = notificationSendingService;
    }

    public UploadResult<AttendanceUpload> handleUpload(MultipartFile file) throws IOException {
        return attendanceUploadAdapter.fileToUpload(file);
    }

    public Confirmation<AttendanceUpload> confirmUpload(CourseInCharge courseInCharge, UploadResult<AttendanceUpload> uploadResult) {
        return attendanceUploadAdapter.uploadToConfirmation(courseInCharge, uploadResult);
    }

    @Transactional
    public void updateAttendance(CourseInCharge courseInCharge, List<AttendanceUpload> uploadList) {
        List<CourseRegistration> courseRegistrations = courseInChargeService.getCourseRegistrations(courseInCharge);

        for (AttendanceUpload attendanceUpload : uploadList) {
            if (AttendanceUploadAdapter.studentExists(attendanceUpload, courseRegistrations, null) == null) {
                log.error("Force updating attendance of invalid student {} {} {}", courseInCharge.getCode(), attendanceUpload.getEnrolment_no());
                throw new RuntimeException("Invalid DataBody : " + attendanceUpload);
            }
        }

        for (AttendanceUpload attendance : uploadList) {
            courseRegistrationService.setAttendance(courseInCharge.getFloatedCourse().getCourse(), attendance);
        }

        sendNotification(courseInCharge.getFacultyMember(), courseInCharge.getFloatedCourse().getCourse());
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
