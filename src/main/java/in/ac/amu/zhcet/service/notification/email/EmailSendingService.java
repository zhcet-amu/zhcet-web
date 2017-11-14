package in.ac.amu.zhcet.service.notification.email;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.upload.AttendanceUpload;
import in.ac.amu.zhcet.service.StudentService;
import in.ac.amu.zhcet.service.notification.email.data.LinkMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class EmailSendingService {

    private final LinkMailService linkMailService;
    private final StudentService studentService;

    @Autowired
    public EmailSendingService(LinkMailService linkMailService, StudentService studentService) {
        this.linkMailService = linkMailService;
        this.studentService = studentService;
    }

    @Async
    public void sendEmailsForAttendance(Course course, List<AttendanceUpload> uploadList) {
        log.info("Email Attendance Notification : {}", course.getCode());

        Stream<Student> students = uploadList
                .parallelStream()
                .map(AttendanceUpload::getEnrolment_no)
                .map(studentService::getByEnrolmentNumber);

        LinkMessage payLoad = getPayLoad(course.getCode(), course.getTitle());
        StudentService.verifiedUsers(students)
                .forEach(userAuth -> {
                    payLoad.setRecipient(userAuth.getEmail());
                    payLoad.setName(userAuth.getName());

                    linkMailService.sendEmail(payLoad);
                });
    }

    private LinkMessage getPayLoad(String courseId, String courseName) {
        return LinkMessage.builder()
                .title("Attendance Update")
                .subject("ZHCET Course " + courseId + " Attendance Updated")
                .relativeLink("/student/attendance")
                .linkText("View Attendance")
                .preMessage("Your attendance for course <strong>" + courseId + " : " + courseName + "</strong> has just been updated." +
                        "<br>Please click the button below to view your attendance")
                .build();
    }
}
