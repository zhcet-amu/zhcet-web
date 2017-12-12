package in.ac.amu.zhcet.controller.dean;

import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.upload.FacultyUpload;
import in.ac.amu.zhcet.data.model.dto.upload.StudentUpload;
import in.ac.amu.zhcet.data.model.file.PasswordFile;
import in.ac.amu.zhcet.service.realtime.RealTimeStatus;
import in.ac.amu.zhcet.service.upload.storage.FileSystemStorageService;
import in.ac.amu.zhcet.service.upload.csv.faculty.FacultyUploadService;
import in.ac.amu.zhcet.service.upload.csv.student.StudentUploadService;
import in.ac.amu.zhcet.service.upload.csv.base.Confirmation;
import in.ac.amu.zhcet.service.upload.csv.base.UploadResult;
import in.ac.amu.zhcet.service.upload.storage.FileType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

@Slf4j
@Controller
public class UserRegistrationController {

    private final StudentUploadService studentUploadService;
    private final FacultyUploadService facultyUploadService;
    private final FileSystemStorageService systemStorageService;

    @Autowired
    public UserRegistrationController(StudentUploadService studentUploadService, FacultyUploadService facultyUploadService, FileSystemStorageService systemStorageService) {
        this.studentUploadService = studentUploadService;
        this.facultyUploadService = facultyUploadService;
        this.systemStorageService = systemStorageService;
    }

    @PostMapping("/dean/register/students")
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

    @PostMapping("/dean/register/students/confirm")
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

    @PostMapping("/dean/register/faculty")
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

    @PostMapping("/dean/register/faculty/confirm")
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

    @GetMapping("/dean/password/{id}")
    public void downloadCsv(HttpServletResponse response, @PathVariable("id") PasswordFile passwordFile) throws IOException {
        if (passwordFile == null || passwordFile.isExpired()) return;

        response.setContentType("text/csv");

        response.setHeader("Content-disposition", "attachment;filename=passwords.csv");

        List<String> lines = Files.readAllLines(systemStorageService.load(FileType.CSV, passwordFile.getLink()));
        for (String line : lines) {
            response.getOutputStream().println(line);
        }

        response.getOutputStream().flush();
    }


}
