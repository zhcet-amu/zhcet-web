package in.ac.amu.zhcet.controller.faculty;

import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.service.core.FacultyService;
import in.ac.amu.zhcet.service.core.FloatedCourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Slf4j
@Controller
public class FloatedCourseController {
    private final FacultyService facultyService;
    private final FloatedCourseService floatedCourseService;

    @Autowired
    public FloatedCourseController(FacultyService facultyService, FloatedCourseService floatedCourseService) {
        this.facultyService = facultyService;
        this.floatedCourseService = floatedCourseService;
    }

    @GetMapping("/faculty/courses")
    public String getCourse(Model model) {
        model.addAttribute("page_title", "Course Management");
        model.addAttribute("page_subtitle", "Faculty Floated Course Management");
        model.addAttribute("page_description", "Manage and upload attendance for currently floated courses");
        String selected = "";
        FacultyMember facultyMember = facultyService.getLoggedInMember();
        List<FloatedCourse> floatedCourses = floatedCourseService.getByFaculty(facultyMember);
        model.addAttribute("floatedCourses", floatedCourses);
        model.addAttribute("selectedCourse", selected);
        return "faculty_courses";
    }

    @GetMapping("faculty/courses/{id}")
    public String getStudents(Model model, @PathVariable String id) {
        FloatedCourse floatedCourse = floatedCourseService.getCourseById(id);

        if (floatedCourse != null) {
            model.addAttribute("page_title", floatedCourse.getCourse().getTitle());
            model.addAttribute("page_subtitle", "Attendance management for " + floatedCourse.getCourse().getCode());
            model.addAttribute("page_description", "Upload attendance for the floated course");

            List<CourseRegistration> courseRegistrations = floatedCourse.getCourseRegistrations();
            model.addAttribute("courseRegistrations", courseRegistrations);
            model.addAttribute("course_id", id);
        }

        return "course_attendance";
    }
}