package in.ac.amu.zhcet.service;

import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.AttendanceUpload;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.service.core.ConfigurationService;
import in.ac.amu.zhcet.service.core.CourseManagementService;
import in.ac.amu.zhcet.service.core.StudentService;
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
    private final CourseManagementService courseManagementService;

    @Autowired
    public EmailNotificationService(ConfigurationService configurationService, EmailService emailService, StudentService studentService, CourseManagementService courseManagementService) {
        this.configurationService = configurationService;
        this.emailService = emailService;
        this.studentService = studentService;
        this.courseManagementService = courseManagementService;
    }

    @Async
    public void sendNotificationsForAttendance(String id, List<AttendanceUpload> uploadList) {
        String url = configurationService.getBaseUrl() + "/attendance";
        log.info("Email Attendance Notification : " + id);
        log.info("URL : " + url);
        FloatedCourse floatedCourse = courseManagementService.getFloatedCourseByCode(id);

        if (floatedCourse == null) {
            log.warn("Email request for invalid course " + id);
            return;
        }

        List<String> emails = uploadList
                .parallelStream()
                .map(AttendanceUpload::getStudent)
                .map(studentService::getByEnrolmentNumber)
                .map(Student::getUser)
                .filter(userAuth -> userAuth.isActive() && !userAuth.isEmailUnsubscribed())
                .map(UserAuth::getEmail)
                .collect(Collectors.toList());

        if (emails.isEmpty()) {
            log.info("No activated subscribed student found for emailing...");
        }

        String recipient = emails.get(0);
        log.info("Sending email to " + recipient);
        String[] bcc = null;
        if (emails.size() > 1) {
            List<String> others = emails.subList(1, emails.size());
            log.info("BCC : " + others.toString());
            bcc = others.toArray(new String[others.size()]);
        }
        String html = getHtml(id, floatedCourse.getCourse().getTitle(), url);
        emailService.sendHtmlMail(recipient, "ZHCET Course " + id + " Attendance Updated", html, bcc);
    }

    private String getHtml(String courseId, String courseName, String url) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", "Attendance Update");
        map.put("link", url);
        map.put("link_text", "View Attendance");
        map.put("pre_message", "Your attendance for course <strong>" + courseId + " : " + courseName + "</strong> has just been updated." +
                "<br>Please click the button below to view your attendance");

        return emailService.render("html/link", map);
    }
}
