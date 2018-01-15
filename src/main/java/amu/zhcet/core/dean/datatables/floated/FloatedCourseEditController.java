package amu.zhcet.core.dean.datatables.floated;

import amu.zhcet.common.flash.Flash;
import amu.zhcet.common.utils.SortUtils;
import amu.zhcet.common.utils.Utils;
import amu.zhcet.core.error.ErrorUtils;
import amu.zhcet.data.config.ConfigurationService;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.course.floated.FloatedCourseNotFoundException;
import amu.zhcet.data.course.floated.FloatedCourseService;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.data.course.registration.CourseRegistrationService;
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
@RequestMapping("/dean/floated")
public class FloatedCourseEditController {

    private final FloatedCourseService floatedCourseService;
    private final CourseRegistrationService courseRegistrationService;

    @Autowired
    public FloatedCourseEditController(FloatedCourseService floatedCourseService, CourseRegistrationService courseRegistrationService) {
        this.floatedCourseService = floatedCourseService;
        this.courseRegistrationService = courseRegistrationService;
    }

    @GetMapping
    public String students(Model model) {
        model.addAttribute("page_title", "Floated Courses - " + Utils.getSessionName(ConfigurationService.getDefaultSessionCode()));
        model.addAttribute("page_subtitle", "This session's floated courses");
        model.addAttribute("page_description", "Search and view this session's floated courses for all departments");
        return "dean/floated_page";
    }

    @GetMapping("/{course}")
    public String courseDetail(Model model, @PathVariable Course course, WebRequest webRequest) {
        FloatedCourse floatedCourse = floatedCourseService.getFloatedCourse(course).orElseThrow(FloatedCourseNotFoundException::new);

        if (!model.containsAttribute("success"))
            webRequest.removeAttribute("confirmRegistration", RequestAttributes.SCOPE_SESSION);

        model.addAttribute("page_title", course.getCode() + " - " + course.getTitle());
        model.addAttribute("page_subtitle", "Course management for " + course.getCode());
        model.addAttribute("page_description", "Register Students for the Floated course");

        List<CourseRegistration> courseRegistrations = floatedCourse.getCourseRegistrations();
        List<String> emails = FloatedCourseService
                .getEmailsFromCourseRegistrations(courseRegistrations.stream())
                .collect(Collectors.toList());
        SortUtils.sortCourseAttendance(courseRegistrations);
        model.addAttribute("courseRegistrations", courseRegistrations);
        model.addAttribute("floatedCourse", floatedCourse);
        model.addAttribute("deanOverride", "dean");
        model.addAttribute("email_list", emails);

        return "dean/floated_course";
    }

    @PostMapping("/{course}/unregister")
    public String removeStudent(RedirectAttributes attributes, @PathVariable Course course, @RequestParam Student student) {
        // TODO: Extract to shared package
        ErrorUtils.requireNonNullCourse(course);
        ErrorUtils.requireNonNullStudent(student);
        FloatedCourse floatedCourse = floatedCourseService.getFloatedCourse(course).orElseThrow(FloatedCourseNotFoundException::new);
        courseRegistrationService.removeRegistration(floatedCourse, student);
        attributes.addFlashAttribute("flash_messages", Flash.success("Student removed from course"));

        return "redirect:/dean/floated/{course}";
    }

}
