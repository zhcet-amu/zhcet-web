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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class FloatCourseController {

    private final CourseManagementService courseManagementService;

    @Autowired
    public FloatCourseController(CourseManagementService courseManagementService) {
        this.courseManagementService = courseManagementService;
    }

    @PreAuthorize("isOfDepartment(#department, #course)")
    @GetMapping("department/{department}/courses/{course}/float")
    public String floatCourse(@PathVariable Department department, @PathVariable Course course, RedirectAttributes redirectAttributes) {
        FloatedCourse floatedCourse = courseManagementService.getFloatedCourseByCourse(course);

        if (floatedCourse != null) {
            log.warn("Course is already floated {}", course.getCode());
            redirectAttributes.addFlashAttribute("float_error", "Course is already floated");
        }  else {
            redirectAttributes.addFlashAttribute("courses", Collections.singletonList(course));
        }

        return "redirect:/department/{department}/course/float";
    }

    @PreAuthorize("isDepartment(#department)")
    @GetMapping("/department/{department}/course/float")
    public String floatCourse(Model model, @PathVariable Department department) {
        model.addAttribute("page_title", "Float Course : " + department.getName() + " Department");
        model.addAttribute("page_subtitle", "Floated Course Management");
        model.addAttribute("page_description", "Float and manage course and faculty in-charge for this session");
        model.addAttribute("department", department);

        return "department/float_course";
    }

    @PreAuthorize("isDepartment(#department)")
    @PostMapping("/department/{department}/course/float")
    public String floatCourses(RedirectAttributes redirectAttributes, @PathVariable Department department, @RequestParam("code") List<String> codes) {
        String redirectLink = "redirect:/department/{department}/course/float";
        List<Course> courses = courseManagementService.getAllActiveCourse(department, true);

        if (!courses.stream().map(Course::getCode).collect(Collectors.toList()).containsAll(codes)) {
            log.warn("Has an invalid Course Codes : Floating {}", codes.toString());
            redirectAttributes.addFlashAttribute("float_error", "Invalid Course Code!");
            return redirectLink;
        }

        List<FloatedCourse> floatedCourses = courseManagementService.getCurrentFloatedCourses(department);

        if (floatedCourses.stream().map(floatedCourse -> floatedCourse.getCourse().getCode()).anyMatch(codes::contains)) {
            log.warn("Some courses already floated : {}", codes.toString());
            redirectAttributes.addFlashAttribute("float_error", "Some courses are already floated!");
            return redirectLink;
        }

        List<Course> courseList = courses.stream()
                .filter(course -> codes.contains(course.getCode()))
                .collect(Collectors.toList());

        for (Course course : courseList)
            courseManagementService.floatCourse(course);

        redirectAttributes.addFlashAttribute("float_success", "Courses floated successfully!");

        if (courseList.size() == 1)
            return String.format("redirect:/department/{department}/floated/%s", courseList.get(0).getCode());

        return "redirect:/department/{department}/course/float";
    }

}
