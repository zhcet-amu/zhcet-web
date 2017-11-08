package in.ac.amu.zhcet.service.notification;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.upload.AttendanceUpload;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.service.StudentService;
import in.ac.amu.zhcet.service.ConfigurationService;
import in.ac.amu.zhcet.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmailNotificationService {

    private final ConfigurationService configurationService;
    private final EmailService emailService;
    private final StudentService studentService;

    @Autowired
    public EmailNotificationService(ConfigurationService configurationService, EmailService emailService, StudentService studentService) {
        this.configurationService = configurationService;
        this.emailService = emailService;
        this.studentService = studentService;
    }

    @Async
    public void sendNotificationsForAttendance(Course course, List<AttendanceUpload> uploadList) {
        String url = configurationService.getBaseUrl() + "/student/attendance";
        log.info("Email Attendance Notification : {}", course.getCode());
        log.info("URL : {}", url);

        List<Student> students = uploadList
                .parallelStream()
                .map(AttendanceUpload::getEnrolment_no)
                .map(studentService::getByEnrolmentNumber)
                .collect(Collectors.toList());

        List<String> emails = getEmails(students);


        if (emails.isEmpty()) {
            log.warn("No activated subscribed student found for emailing...");
        }

        for (String recipient : emails) {
            log.info("Sending email to " + recipient);

            String unsubscribeUrl = configurationService.getBaseUrl() + "/login/unsubscribe?email="
                    + recipient + "&conf=" + SecurityUtils.getHash(recipient);

            log.info("Unsubscribe Link {}", unsubscribeUrl);
            String html = getHtml(course.getCode(), course.getTitle(), url, unsubscribeUrl);
            emailService.sendHtmlMail(recipient, "ZHCET Course " + course.getCode() + " Attendance Updated", html, null);
        }
    }

    private String getHtml(String courseId, String courseName, String url, String unsubcribeUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", "Attendance Update");
        map.put("link", url);
        map.put("link_text", "View Attendance");
        map.put("pre_message", "Your attendance for course <strong>" + courseId + " : " + courseName + "</strong> has just been updated." +
                "<br>Please click the button below to view your attendance");
        map.put("unsubscribe_link", unsubcribeUrl);

        return emailService.render("html/link", map);
    }

    public List<String> getEmails(List<Student> students) {

        return students.stream()
                .map(Student::getUser)
                .filter(userAuth -> userAuth.isEmailVerified() && !userAuth.isEmailUnsubscribed())
                .map(UserAuth::getEmail)
                .collect(Collectors.toList());
    }
}
