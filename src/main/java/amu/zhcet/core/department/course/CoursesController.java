package amu.zhcet.core.department.course;

import amu.zhcet.common.page.Path;
import amu.zhcet.common.page.PathChain;
import amu.zhcet.core.department.DepartmentController;
import amu.zhcet.core.error.ErrorUtils;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.CourseService;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.course.floated.FloatedCourseService;
import amu.zhcet.data.department.Department;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Controller
@RequestMapping("/admin/department/{department}/courses")
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
                    .link(String.format("/admin/department/%s/courses", department.getCode()))
                    .build());
    }

    @GetMapping
    public String getCourses(Model model, @PathVariable Department department, @RequestParam(value = "all", required = false) Boolean all) {
        ErrorUtils.requireNonNullDepartment(department);

        // Determine if only active courses have to be should
        boolean active = !(all != null && all);

        model.addAttribute("page_description", "View and manage courses for the Department");
        model.addAttribute("department", department);
        model.addAttribute("page_title", "Courses : " + department.getName() + " Department");
        model.addAttribute("page_subtitle", "Course Management");
        model.addAttribute("page_path", getPath(department));
        model.addAttribute("all", !active);

        List<FloatedCourse> floatedCourses = floatedCourseService.getCurrentFloatedCourses(department);

        List<Course> courses = courseService.getAllActiveCourse(department, active);

        // Add meta tag and no of registrations to each course
        for (FloatedCourse floatedCourse : floatedCourses) {
            Stream.of(floatedCourse)
                    .map(FloatedCourse::getCourse)
                    .map(courses::indexOf)
                    .filter(index -> index != -1)
                    .map(courses::get)
                    .findFirst()
                    .ifPresent(course -> {
                        course.setMeta("Floated");
                        course.setRegistrations(floatedCourse.getCourseRegistrations().size());
                    });
        }

        courses.sort(Comparator.comparing(Course::getCode));

        model.addAttribute("courses", courses);

        return "department/courses";
    }

}
