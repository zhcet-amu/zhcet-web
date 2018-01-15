package amu.zhcet.core.department.floated;

import amu.zhcet.common.flash.Flash;
import amu.zhcet.common.page.Path;
import amu.zhcet.common.page.PathChain;
import amu.zhcet.common.utils.SortUtils;
import amu.zhcet.core.error.ErrorUtils;
import amu.zhcet.core.department.course.CoursesController;
import amu.zhcet.data.course.floated.FloatedCourseNotFoundException;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.course.floated.FloatedCourseService;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.data.course.registration.CourseRegistrationService;
import amu.zhcet.data.department.Department;
import amu.zhcet.data.user.student.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("department/{department}/floated/{course}")
public class FloatedCourseController {

    private final CourseRegistrationService courseRegistrationService;
    private final FloatedCourseService floatedCourseService;

    @Autowired
    public FloatedCourseController(CourseRegistrationService courseRegistrationService, FloatedCourseService floatedCourseService) {
        this.courseRegistrationService = courseRegistrationService;
        this.floatedCourseService = floatedCourseService;
    }

    public static PathChain getPath(Department department, Course course) {
        return CoursesController.getPath(department)
                .add(Path.builder().title(course.getCode())
                        .link(String.format("/department/%s/courses/%s/edit", department.getCode(), course.getCode()))
                        .build())
                .add(Path.builder().title("Manage")
                        .link(String.format("/department/%s/floated/%s", department.getCode(), course.getCode()))
                        .build());
    }

    @GetMapping
    public String courseDetail(Model model, @PathVariable Department department, @PathVariable Course course, WebRequest webRequest) {
        ErrorUtils.requireNonNullDepartment(department);
        FloatedCourse floatedCourse = floatedCourseService.getFloatedCourse(course).orElseThrow(FloatedCourseNotFoundException::new);

        if (!model.containsAttribute("success"))
            webRequest.removeAttribute("confirmRegistration", RequestAttributes.SCOPE_SESSION);

        model.addAttribute("page_title", course.getCode() + " - " + course.getTitle());
        model.addAttribute("page_subtitle", "Course management for " + course.getCode());
        model.addAttribute("page_description", "Register Students and add Faculty In-Charge for the course");
        model.addAttribute("page_path", getPath(department, course));

        List<CourseRegistration> courseRegistrations = floatedCourse.getCourseRegistrations();
        List<String> emails = FloatedCourseService
                .getEmailsFromCourseRegistrations(courseRegistrations.stream())
                .collect(Collectors.toList());
        SortUtils.sortCourseAttendance(courseRegistrations);
        model.addAttribute("courseRegistrations", courseRegistrations);
        model.addAttribute("floatedCourse", floatedCourse);
        model.addAttribute("sections", FloatedCourseService.getSections(floatedCourse));
        model.addAttribute("email_list", emails);

        return "department/floated_course";
    }

    @PostMapping("/unregister")
    public String removeStudent(RedirectAttributes attributes, @PathVariable Department department, @PathVariable Course course, @RequestParam Student student) {
        ErrorUtils.requireNonNullDepartment(department);
        ErrorUtils.requireNonNullStudent(student);
        FloatedCourse floatedCourse = floatedCourseService.getFloatedCourse(course).orElseThrow(FloatedCourseNotFoundException::new);

        courseRegistrationService.removeRegistration(floatedCourse, student);
        attributes.addFlashAttribute("flash_messages", Flash.success("Student removed from course"));

        return "redirect:/department/{department}/floated/{course}";
    }

    @PostMapping("/unfloat")
    public String unfloat(RedirectAttributes redirectAttributes, @PathVariable Department department, @PathVariable Course course) {
        ErrorUtils.requireNonNullDepartment(department);
        FloatedCourse floatedCourse = floatedCourseService.getFloatedCourse(course).orElseThrow(FloatedCourseNotFoundException::new);

        floatedCourseService.unfloatCourse(floatedCourse);
        redirectAttributes.addFlashAttribute("course_success", "Course " + course.getCode() + " unfloated successfully!");

        return "redirect:/department/{department}/courses?active=true";
    }

}
