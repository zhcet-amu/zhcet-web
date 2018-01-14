package amu.zhcet.core.dean.registration.faculty;

import amu.zhcet.common.realtime.RealTimeStatus;
import amu.zhcet.data.user.faculty.FacultyMember;
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
@RequestMapping("/dean/register/faculty")
public class FacultyRegistrationController {

    private final FacultyUploadService facultyUploadService;

    @Autowired
    public FacultyRegistrationController(FacultyUploadService facultyUploadService) {
        this.facultyUploadService = facultyUploadService;
    }

    @PostMapping
    public String uploadFacultyFile(RedirectAttributes attributes, @RequestParam MultipartFile file, HttpSession session, WebRequest webRequest) throws IOException {
        try {
            UploadResult<FacultyUpload> result = facultyUploadService.handleUpload(file);

            if (!result.getErrors().isEmpty()) {
                webRequest.removeAttribute("confirmFacultyRegistration", RequestAttributes.SCOPE_SESSION);
                attributes.addFlashAttribute("faculty_errors", result.getErrors());
            } else {
                attributes.addFlashAttribute("faculty_success", true);
                Confirmation<FacultyMember> confirmation = facultyUploadService.confirmUpload(result);
                session.setAttribute("confirmFacultyRegistration", confirmation);
            }
        } catch (IOException ioe) {
            log.error("Error registering faculty", ioe);
        }

        return "redirect:/dean";
    }

    @PostMapping("/confirm")
    public String uploadFaculty(RedirectAttributes attributes, HttpSession session, WebRequest webRequest) {
        Confirmation<FacultyMember> confirmation = (Confirmation<FacultyMember>) session.getAttribute("confirmFacultyRegistration");

        if (confirmation == null || !confirmation.getErrors().isEmpty()) {
            attributes.addFlashAttribute("errors", Collections.singletonList("Unknown Error"));
        } else {
            try {
                RealTimeStatus status = facultyUploadService.registerFaculty(confirmation);
                attributes.addFlashAttribute("task_id_faculty", status.getId());
                attributes.addFlashAttribute("file_saved", status.getMeta());
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

        return "redirect:/dean";
    }

}
