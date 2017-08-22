package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.*;
import in.ac.amu.zhcet.data.service.upload.AttendanceUploadService;
import in.ac.amu.zhcet.data.service.FacultyService;
import in.ac.amu.zhcet.data.service.FloatedCourseService;
import in.ac.amu.zhcet.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
public class FacultyController {
    private final FacultyService facultyService;
    private final FloatedCourseService floatedCourseService;
    private final AttendanceUploadService attendanceUploadService;

    @Autowired
    public FacultyController(FacultyService facultyService, FloatedCourseService floatedCourseService, AttendanceUploadService attendanceUploadService) {
        this.facultyService = facultyService;
        this.floatedCourseService = floatedCourseService;
        this.attendanceUploadService = attendanceUploadService;
    }

    @GetMapping("/faculty/courses")
    public String getCourse(Model model) {
        String selected = "";
        FacultyMember facultyMember = facultyService.getLoggedInMember();
        List<FloatedCourse> floatedCourses = floatedCourseService.getByFaculty(facultyMember);
        model.addAttribute("floatedCourses", floatedCourses);
        model.addAttribute("selectedCourse", selected);
        return "floated_course";
    }

    @GetMapping("faculty/courses/{id}")
    public String getStudents(Model model, @PathVariable String id) {
        FloatedCourse floatedCourse = floatedCourseService.getCourseById(id);
        List<CourseRegistration> courseRegistrations = floatedCourse.getCourseRegistrations();
        model.addAttribute("courseRegistrations", courseRegistrations);
        model.addAttribute("course_id", id);
        model.addAttribute("currentSession", Utils.getCurrentSession());
        return "course_attendance";
    }

    @PostMapping("faculty/courses/{id}/attendance")
    public String uploadFile(RedirectAttributes attributes, @PathVariable String id, @RequestParam("file") MultipartFile file, HttpSession session, WebRequest webRequest) {
        try {
            AttendanceUploadService.UploadResult result = attendanceUploadService.handleUpload(file);

            if (!result.getErrors().isEmpty()) {
                attributes.addFlashAttribute("errors", result.getErrors());
                webRequest.removeAttribute("confirmAttendance" + id, RequestAttributes.SCOPE_SESSION);
            } else {
                attributes.addFlashAttribute("success", true);
                AttendanceUploadService.AttendanceConfirmation confirmation = attendanceUploadService.confirmUpload(id, result);
                session.setAttribute("confirmAttendance" + id, confirmation);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return "redirect:/faculty/courses/{id}";
    }

    @PostMapping("faculty/courses/{id}/attendance_confirmed")
    public String uploadAttendance(RedirectAttributes attributes, @PathVariable String id, HttpSession session, WebRequest webRequest) {
        AttendanceUploadService.AttendanceConfirmation confirmation =
                (AttendanceUploadService.AttendanceConfirmation) session.getAttribute("confirmAttendance" + id);

        if (confirmation == null || !confirmation.getErrors().isEmpty()) {
            attributes.addFlashAttribute("errors", Collections.singletonList("Unknown Error"));
        } else {
            attendanceUploadService.updateAttendance(id, confirmation);
            webRequest.removeAttribute("confirmAttendance" + id, RequestAttributes.SCOPE_SESSION);
            attributes.addFlashAttribute("updated", true);
        }

        return "redirect:/faculty/courses/{id}";
    }
}
