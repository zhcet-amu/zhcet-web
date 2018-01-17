package amu.zhcet.core.dean.registration.faculty;

import amu.zhcet.common.realtime.RealTimeStatus;
import amu.zhcet.common.realtime.RealTimeStatusService;
import amu.zhcet.data.user.faculty.FacultyMember;
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
@RequestMapping("/admin/dean/register/faculty")
public class FacultyRegistrationController {

    public static final String KEY_FACULTY_REGISTRATION = "confirmFacultyRegistration";
    private final FacultyUploadService facultyUploadService;
    private final RealTimeStatusService realTimeStatusService;

    @Autowired
    public FacultyRegistrationController(FacultyUploadService facultyUploadService, RealTimeStatusService realTimeStatusService) {
        this.facultyUploadService = facultyUploadService;
        this.realTimeStatusService = realTimeStatusService;
    }

    @PostMapping
    public String uploadFacultyFile(RedirectAttributes attributes, @RequestParam MultipartFile file, HttpSession session, WebRequest webRequest) throws IOException {
        try {
            UploadResult<FacultyUpload> result = facultyUploadService.handleUpload(file);

            if (!result.getErrors().isEmpty()) {
                webRequest.removeAttribute(KEY_FACULTY_REGISTRATION, RequestAttributes.SCOPE_SESSION);
                attributes.addFlashAttribute("faculty_errors", result.getErrors());
            } else {
                attributes.addFlashAttribute("faculty_success", true);
                Confirmation<FacultyMember> confirmation = facultyUploadService.confirmUpload(result);
                session.setAttribute(KEY_FACULTY_REGISTRATION, confirmation);
            }
        } catch (IOException ioe) {
            log.error("Error registering faculty", ioe);
        }

        return "redirect:/admin/dean";
    }

    @PostMapping("/confirm")
    public String uploadFaculty(RedirectAttributes attributes,
                                @SessionAttribute(KEY_FACULTY_REGISTRATION) Confirmation<FacultyMember> confirmation,
                                WebRequest webRequest) {
        if (confirmation == null || !confirmation.getErrors().isEmpty()) {
            attributes.addFlashAttribute("errors", Collections.singletonList("Unknown Error"));
        } else {
            try {
                String passwordFileLocation = facultyUploadService.savePasswordFile(confirmation);
                RealTimeStatus status = realTimeStatusService.install();
                facultyUploadService.registerFaculty(confirmation, status);
                attributes.addFlashAttribute("task_id_faculty", status.getId());
                attributes.addFlashAttribute("file_saved", passwordFileLocation);
                attributes.addFlashAttribute("faculty_registered", true);
            } catch (IOException e) {
                log.error("Error registering faculty", e);
                attributes.addFlashAttribute("file_error", true);
            } catch (Exception e) {
                log.error("Error registering faculty", e);
                attributes.addFlashAttribute("faculty_unknown_error", true);
            }

            webRequest.removeAttribute("confirmFacultyRegistration", RequestAttributes.SCOPE_SESSION);
        }

        return "redirect:/admin/dean";
    }

}
