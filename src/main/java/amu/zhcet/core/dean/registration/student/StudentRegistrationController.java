package amu.zhcet.core.dean.registration.student;

import amu.zhcet.common.realtime.RealTimeStatus;
import amu.zhcet.data.user.student.Student;
import amu.zhcet.storage.csv.Confirmation;
import amu.zhcet.storage.csv.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;

@Slf4j
@Controller
@RequestMapping("/dean/register/students")
public class StudentRegistrationController {

    private final StudentUploadService studentUploadService;

    @Autowired
    public StudentRegistrationController(StudentUploadService studentUploadService) {
        this.studentUploadService = studentUploadService;
    }

    @PostMapping
    public String uploadFile(RedirectAttributes attributes, @RequestParam MultipartFile file, HttpSession session, WebRequest webRequest) {
        try {
            UploadResult<StudentUpload> result = studentUploadService.handleUpload(file);

            if (!result.getErrors().isEmpty()) {
                webRequest.removeAttribute("confirmStudentRegistration", RequestAttributes.SCOPE_SESSION);
                attributes.addFlashAttribute("students_errors", result.getErrors());
            } else {
                attributes.addFlashAttribute("students_success", true);
                Confirmation<Student> confirmation = studentUploadService.confirmUpload(result);

                session.setAttribute("confirmStudentRegistration", confirmation);
            }
        } catch (IOException ioe) {
            log.error("Error registering students", ioe);
        }

        return "redirect:/dean";
    }

    @PostMapping("/confirm")
    public String uploadStudents(RedirectAttributes attributes, HttpSession session, WebRequest webRequest) {
        Confirmation<Student> confirmation = (Confirmation<Student>) session.getAttribute("confirmStudentRegistration");

        if (confirmation == null || !confirmation.getErrors().isEmpty()) {
            attributes.addFlashAttribute("errors", Collections.singletonList("Unknown Error"));
        } else {
            try {
                RealTimeStatus status = studentUploadService.registerStudents(confirmation);
                attributes.addFlashAttribute("task_id_student", status.getId());
                attributes.addFlashAttribute("students_registered", true);
            } catch (Exception e) {
                log.error("Error registering students", e);
                attributes.addFlashAttribute("student_unknown_error", true);
            }

            webRequest.removeAttribute("confirmStudentRegistration", RequestAttributes.SCOPE_SESSION);
        }

        return "redirect:/dean";
    }

}
