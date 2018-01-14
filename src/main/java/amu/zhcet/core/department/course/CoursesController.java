package amu.zhcet.core.department.course;

import amu.zhcet.common.page.Path;
import amu.zhcet.common.page.PathChain;
import amu.zhcet.core.department.DepartmentController;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.CourseService;
import amu.zhcet.data.course.floated.FloatedCourseService;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.department.Department;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final CourseService courseService;
    private final FloatedCourseService floatedCourseService;

    @Autowired
    public CoursesController(CourseService courseService, FloatedCourseService floatedCourseService) {
        this.courseService = courseService;
        this.floatedCourseService = floatedCourseService;
    }

    public static PathChain getPath(Department department) {
        return DepartmentController.getPath(department)
                .add(Path.builder().title("Courses")
                    .link(String.format("/department/%s/courses", department.getCode()))
                    .build());
    }

    @GetMapping("/department/{department}/courses")
    public String getCourses(Model model, @PathVariable Department department, @RequestParam(value = "all", required = false) Boolean all) {
        String templateUrl = "department/courses";
        if (department == null)
            return templateUrl;

        boolean active = !(all != null && all);

        model.addAttribute("page_description", "View and manage courses for the Department");
        model.addAttribute("department", department);
        model.addAttribute("page_title", "Courses : " + department.getName() + " Department");
        model.addAttribute("page_subtitle", "Course Management");
        model.addAttribute("page_path", getPath(department));
        model.addAttribute("all", !active);

        List<Course> floatedCourses = floatedCourseService.getCurrentFloatedCourses(department)
                .stream()
                .map(FloatedCourse::getCourse)
                .collect(Collectors.toList());

        List<Course> courses = courseService.getAllActiveCourse(department, active);
        courses.forEach(course -> {
            if (floatedCourses.contains(course))
                course.setMeta("Floated");
        });

        courses.sort(Comparator.comparing(Course::getCode));

        model.addAttribute("courses", courses);

        return templateUrl;
    }

}
