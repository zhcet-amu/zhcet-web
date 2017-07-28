package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.*;
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

import java.io.IOException;
import java.util.List;

@Controller
public class FacultyController {
    private final FacultyService facultyService;
    private final FloatedCourseService floatedCourseService;

    @Autowired
    public FacultyController(FacultyService facultyService, FloatedCourseService floatedCourseService) {
        this.facultyService = facultyService;
        this.floatedCourseService = floatedCourseService;
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
    public String uploadFile(Model model, @PathVariable String id, @RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                String completeData = new String(bytes);
                System.out.print(completeData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "redirect:/faculty/courses/{id}";
    }
}
