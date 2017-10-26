package in.ac.amu.zhcet.controller.department;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.service.CourseManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class CoursesController {

    private final CourseManagementService courseManagementService;

    @Autowired
    public CoursesController(CourseManagementService courseManagementService) {
        this.courseManagementService = courseManagementService;
    }

    @PreAuthorize("isDepartment(#department)")
    @GetMapping("/department/{department}/courses")
    public String getCourses(Model model, @PathVariable Department department, @RequestParam(value = "active", required = false) Boolean active) {
        if (active == null)
            return "redirect:/department/{department}/courses?active=true";

        model.addAttribute("page_description", "View and manage courses for the Department");
        model.addAttribute("department", department);
        model.addAttribute("page_title", "Courses : " + department.getName() + " Department");
        model.addAttribute("page_subtitle", "Course Management");

        List<Course> courses = courseManagementService.getAllActiveCourse(department, active);
        courses.sort(Comparator.comparing(Course::getCode));

        List<Course> floatedCourses = courseManagementService.getCurrentFloatedCourses(department)
                .stream()
                .map(FloatedCourse::getCourse)
                .collect(Collectors.toList());

        courses = courses.stream()
                .map(course -> {
                    if (floatedCourses.contains(course))
                        course.setMeta("Floated");

                    return course;
                }).collect(Collectors.toList());

        model.addAttribute("courses", courses);

        return "department/courses";
    }

}
