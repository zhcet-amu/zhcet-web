package amu.zhcet.core.dean.registration.student;

import amu.zhcet.common.realtime.RealTimeStatus;
import amu.zhcet.common.realtime.RealTimeStatusService;
import amu.zhcet.data.user.student.Student;
import amu.zhcet.storage.csv.Confirmation;
import amu.zhcet.storage.csv.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;

@Slf4j
@Controller
@RequestMapping("/admin/dean/register/students")
public class StudentRegistrationController {

    public static final String KEY_STUDENT_REGISTRATION = "confirmStudentRegistration";
    private final StudentUploadService studentUploadService;
    private final RealTimeStatusService realTimeStatusService;

    @Autowired
    public StudentRegistrationController(StudentUploadService studentUploadService, RealTimeStatusService realTimeStatusService) {
        this.studentUploadService = studentUploadService;
        this.realTimeStatusService = realTimeStatusService;
    }

    @PostMapping
    public String uploadFile(RedirectAttributes attributes, @RequestParam MultipartFile file, HttpSession session, WebRequest webRequest) {
        try {
            UploadResult<StudentUpload> result = studentUploadService.handleUpload(file);

            if (!result.getErrors().isEmpty()) {
                webRequest.removeAttribute(KEY_STUDENT_REGISTRATION, RequestAttributes.SCOPE_SESSION);
                attributes.addFlashAttribute("students_errors", result.getErrors());
            } else {
                attributes.addFlashAttribute("students_success", true);
                Confirmation<Student> confirmation = studentUploadService.confirmUpload(result);

                session.setAttribute(KEY_STUDENT_REGISTRATION, confirmation);
            }
        } catch (IOException ioe) {
            log.error("Error registering students", ioe);
        }

        return "redirect:/admin/dean";
    }

    @PostMapping("/confirm")
    public String uploadStudents(RedirectAttributes attributes,
                                 @SessionAttribute(KEY_STUDENT_REGISTRATION) Confirmation<Student> confirmation,
                                 WebRequest webRequest) {
        if (confirmation == null || !confirmation.getErrors().isEmpty()) {
            attributes.addFlashAttribute("errors", Collections.singletonList("Unknown Error"));
        } else {
            try {
                RealTimeStatus status = realTimeStatusService.install();
                studentUploadService.registerStudents(confirmation, status);
                attributes.addFlashAttribute("task_id_student", status.getId());
                attributes.addFlashAttribute("students_registered", true);
            } catch (Exception e) {
                log.error("Error registering students", e);
                attributes.addFlashAttribute("student_unknown_error", true);
            }

            webRequest.removeAttribute(KEY_STUDENT_REGISTRATION, RequestAttributes.SCOPE_SESSION);
        }

        return "redirect:/admin/dean";
    }

}
