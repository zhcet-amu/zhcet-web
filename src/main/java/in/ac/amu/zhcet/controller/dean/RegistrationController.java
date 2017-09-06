package in.ac.amu.zhcet.controller.dean;

import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.FacultyUpload;
import in.ac.amu.zhcet.data.model.dto.StudentUpload;
import in.ac.amu.zhcet.data.service.file.FileSystemStorageService;
import in.ac.amu.zhcet.data.service.upload.FacultyUploadService;
import in.ac.amu.zhcet.data.service.upload.StudentUploadService;
import in.ac.amu.zhcet.data.service.upload.base.Confirmation;
import in.ac.amu.zhcet.data.service.upload.base.UploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

@Controller
public class RegistrationController {

    private final StudentUploadService studentUploadService;
    private final FacultyUploadService facultyUploadService;
    private final FileSystemStorageService systemStorageService;

    @Autowired
    public RegistrationController(StudentUploadService studentUploadService, FacultyUploadService facultyUploadService, FileSystemStorageService systemStorageService) {
        this.studentUploadService = studentUploadService;
        this.facultyUploadService = facultyUploadService;
        this.systemStorageService = systemStorageService;
    }

    @PostMapping("/dean/register_students")
    public String uploadFile(RedirectAttributes attributes, @RequestParam("file") MultipartFile file, HttpSession session, WebRequest webRequest) {
        try {
            UploadResult<StudentUpload> result = studentUploadService.handleUpload(file);

            if (!result.getErrors().isEmpty()) {
                webRequest.removeAttribute("confirmStudentRegistration", RequestAttributes.SCOPE_SESSION);
                attributes.addFlashAttribute("students_errors", result.getErrors());
            } else {
                attributes.addFlashAttribute("students_success", true);
                Confirmation<Student, String> confirmation = studentUploadService.confirmUpload(result);

                session.setAttribute("confirmStudentRegistration", confirmation);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return "redirect:/dean";
    }

    @PostMapping("/dean/register_students_confirmed")
    public String uploadStudents(RedirectAttributes attributes, HttpSession session, WebRequest webRequest) {
        Confirmation<Student, String> confirmation = (Confirmation<Student, String>) session.getAttribute("confirmStudentRegistration");

        if (confirmation == null || !confirmation.getErrors().isEmpty()) {
            attributes.addFlashAttribute("errors", Collections.singletonList("Unknown Error"));
        } else {
            studentUploadService.registerStudents(confirmation);
            webRequest.removeAttribute("confirmStudentRegistration", RequestAttributes.SCOPE_SESSION);
            attributes.addFlashAttribute("students_registered", true);
        }

        return "redirect:/dean";
    }

    @PostMapping("/dean/register_faculty")
    public String uploadFacultyFile(RedirectAttributes attributes, @RequestParam("file") MultipartFile file, HttpSession session, WebRequest webRequest) throws IOException {
        try {
            UploadResult<FacultyUpload> result = facultyUploadService.handleUpload(file);

            if (!result.getErrors().isEmpty()) {
                webRequest.removeAttribute("confirmFacultyRegistration", RequestAttributes.SCOPE_SESSION);
                attributes.addFlashAttribute("faculty_errors", result.getErrors());
            } else {
                attributes.addFlashAttribute("faculty_success", true);
                Confirmation<FacultyMember, String> confirmation = facultyUploadService.confirmUpload(result);
                session.setAttribute("confirmFacultyRegistration", confirmation);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return "redirect:/dean";
    }

    @PostMapping("/dean/register_faculty_confirmed")
    public String uploadFaculty(RedirectAttributes attributes, HttpSession session, WebRequest webRequest) {
        Confirmation<FacultyMember, String> confirmation = (Confirmation<FacultyMember, String>) session.getAttribute("confirmFacultyRegistration");

        if (confirmation == null || !confirmation.getErrors().isEmpty()) {
            attributes.addFlashAttribute("errors", Collections.singletonList("Unknown Error"));
        } else {
            try {
                String filename = facultyUploadService.registerFaculty(confirmation);
                attributes.addFlashAttribute("file_saved", filename);
            } catch (IOException e) {
                e.printStackTrace();
                attributes.addFlashAttribute("file_error", true);
            } catch (Exception e) {
                e.printStackTrace();
                attributes.addFlashAttribute("unknown_error", true);
            }

            webRequest.removeAttribute("confirmFacultyRegistration", RequestAttributes.SCOPE_SESSION);
            attributes.addFlashAttribute("faculty_registered", true);
        }

        return "redirect:/dean";
    }

    @PostMapping("/dean/clear_session_students")
    public String clearStudentsRegistrationSession(WebRequest webRequest) {
        webRequest.removeAttribute("confirmStudentRegistration", RequestAttributes.SCOPE_SESSION);

        return "redirect:/dean";
    }

    @PostMapping("/dean/clear_session_faculty")
    public String clearFacultyRegistrationSession(WebRequest webRequest) {
        webRequest.removeAttribute("confirmFacultyRegistration", RequestAttributes.SCOPE_SESSION);

        return "redirect:/dean";
    }

    @GetMapping("/dean/download_password")
    public void downloadCsv(HttpServletResponse response, @RequestParam String filename) throws IOException {
        response.setContentType("text/csv");

        response.setHeader("Content-disposition", "attachment;filename=passwords.csv");

        List<String> lines = Files.readAllLines(systemStorageService.load(filename));
        for (String line : lines) {
            response.getOutputStream().println(line);
        }

        response.getOutputStream().flush();
    }


}
