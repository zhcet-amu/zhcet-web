package in.ac.amu.zhcet.controller.department;

import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.dto.upload.RegistrationUpload;
import in.ac.amu.zhcet.service.CourseManagementService;
import in.ac.amu.zhcet.service.CourseRegistrationService;
import in.ac.amu.zhcet.service.csv.RegistrationUploadService;
import in.ac.amu.zhcet.service.csv.base.Confirmation;
import in.ac.amu.zhcet.service.csv.base.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@Controller
public class CourseRegistrationController {

    private final CourseRegistrationService courseRegistrationService;
    private final RegistrationUploadService registrationUploadService;
    private final CourseManagementService courseManagementService;

    public CourseRegistrationController(CourseRegistrationService courseRegistrationService, RegistrationUploadService registrationUploadService, CourseManagementService courseManagementService) {
        this.courseRegistrationService = courseRegistrationService;
        this.registrationUploadService = registrationUploadService;
        this.courseManagementService = courseManagementService;
    }

    @PostMapping("department/floated/{id}/register")
    public String uploadFile(RedirectAttributes attributes, @PathVariable String id, @RequestParam("storage") MultipartFile file, HttpSession session) {
        courseManagementService.verifyAndGetCourse(id);
        try {
            UploadResult<RegistrationUpload> result = registrationUploadService.handleUpload(file);

            if (!result.getErrors().isEmpty()) {
                attributes.addFlashAttribute("errors", result.getErrors());
            } else {
                attributes.addFlashAttribute("success", true);
                Confirmation<CourseRegistration, String> confirmation = registrationUploadService.confirmUpload(id, result);
                session.setAttribute("confirmRegistration", confirmation);
            }
        } catch (IOException ioe) {
            log.error("Error registering students", ioe);
        }

        return "redirect:/department/floated/{id}";
    }

    @PostMapping("department/floated/{id}/register/confirm")
    public String confirmRegistration(RedirectAttributes attributes, @PathVariable String id, HttpSession session, SessionStatus status) {
        courseManagementService.verifyAndGetCourse(id);
        try {
            registrationUploadService.registerStudents(id, (Confirmation<CourseRegistration, String>) session.getAttribute("confirmRegistration"));
            attributes.addFlashAttribute("registered", true);
        } catch (Exception e) {
            log.error("Error confirming student registrations", e);
            attributes.addFlashAttribute("unknown_error", true);
        }

        return "redirect:/department/floated/{id}";
    }

}
