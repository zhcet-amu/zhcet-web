package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.*;
import in.ac.amu.zhcet.data.service.AttendanceUploadService;
import in.ac.amu.zhcet.data.service.FacultyService;
import in.ac.amu.zhcet.data.service.FloatedCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

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
        return "course_attendance";
    }

    @PostMapping("faculty/courses/{id}/attendance")
    public String uploadFile(RedirectAttributes attributes, @PathVariable String id, @RequestParam("file") MultipartFile file) throws IOException {
        try {
            AttendanceUploadService.UploadResult result = attendanceUploadService.handleUpload(file);

            if (!result.getErrors().isEmpty()) {
                attributes.addFlashAttribute("errors", result.getErrors());
            } else {
                attributes.addFlashAttribute("success", true);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return "redirect:/faculty/courses/{id}";
    }
}
