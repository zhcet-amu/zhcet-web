package in.ac.amu.zhcet.service.upload.csv;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.CourseInCharge;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.dto.upload.AttendanceUpload;
import in.ac.amu.zhcet.data.model.notification.ChannelType;
import in.ac.amu.zhcet.data.model.notification.Notification;
import in.ac.amu.zhcet.service.CourseInChargeService;
import in.ac.amu.zhcet.service.CourseRegistrationService;
import in.ac.amu.zhcet.service.notification.NotificationSendingService;
import in.ac.amu.zhcet.service.email.EmailSendingService;
import in.ac.amu.zhcet.service.upload.csv.base.AbstractUploadService;
import in.ac.amu.zhcet.service.upload.csv.base.Confirmation;
import in.ac.amu.zhcet.service.upload.csv.base.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class AttendanceUploadService {

    private boolean existsError;

    private final AbstractUploadService<AttendanceUpload, AttendanceUpload> uploadService;
    private final CourseInChargeService courseInChargeService;
    private final CourseRegistrationService courseRegistrationService;
    private final NotificationSendingService notificationSendingService;

    @Autowired
    public AttendanceUploadService(AbstractUploadService<AttendanceUpload, AttendanceUpload> uploadService, CourseInChargeService courseInChargeService, CourseRegistrationService courseRegistrationService, NotificationSendingService notificationSendingService) {
        this.uploadService = uploadService;
        this.courseInChargeService = courseInChargeService;
        this.courseRegistrationService = courseRegistrationService;
        this.notificationSendingService = notificationSendingService;
    }

    public UploadResult<AttendanceUpload> handleUpload(MultipartFile file) throws IOException {
        return uploadService.handleUpload(AttendanceUpload.class, file);
    }

    private String studentExists(AttendanceUpload upload, List<CourseRegistration> registrations) {
        boolean exists = registrations.stream()
                .map(registration -> registration.getStudent().getEnrolmentNumber())
                .anyMatch(enrolment -> enrolment.equals(upload.getEnrolment_no()));

        if (!exists) {
            log.info("Student does not exist for course in-charge {}", upload.getEnrolment_no());
            existsError = true;
        }

        return exists ? "exists" : null;
    }

    public Confirmation<AttendanceUpload> confirmUpload(CourseInCharge courseInCharge, UploadResult<AttendanceUpload> uploadResult) {
        List<CourseRegistration> courseRegistrations = courseInChargeService.getCourseRegistrations(courseInCharge);

        existsError = false;

        Confirmation<AttendanceUpload> attendanceConfirmation = uploadService.confirmUpload(
                uploadResult,
                item -> item,
                upload -> studentExists(upload, courseRegistrations)
        );

        if (existsError) {
            log.warn(attendanceConfirmation.getData().toString());
            attendanceConfirmation.getErrors().add("The students highlighted in red are not registered for this course");
        }

        return attendanceConfirmation;
    }

    @Transactional
    public void updateAttendance(CourseInCharge courseInCharge, List<AttendanceUpload> uploadList) {
        List<CourseRegistration> courseRegistrations = courseInChargeService.getCourseRegistrations(courseInCharge);

        for (AttendanceUpload attendanceUpload : uploadList) {
            if (studentExists(attendanceUpload, courseRegistrations) == null) {
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
