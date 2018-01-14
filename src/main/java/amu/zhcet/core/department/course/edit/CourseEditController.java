package amu.zhcet.core.department.course.edit;

import amu.zhcet.common.error.UpdateException;
import amu.zhcet.common.page.Path;
import amu.zhcet.common.page.PathChain;
import amu.zhcet.core.department.course.CoursesController;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.CourseService;
import amu.zhcet.data.course.floated.FloatedCourseService;
import amu.zhcet.data.course.CourseType;
import amu.zhcet.data.department.Department;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/department/{department}/courses/{course}")
public class CourseEditController {

    private final CourseService courseService;
    private final FloatedCourseService floatedCourseService;

    @Autowired
    public CourseEditController(CourseService courseService, FloatedCourseService floatedCourseService) {
        this.courseService = courseService;
        this.floatedCourseService = floatedCourseService;
    }

    public static PathChain getPath(Department department, Course course) {
        return CoursesController.getPath(department)
                .add(Path.builder().title(course.getCode())
                        .build())
                .add(Path.builder().title("Edit")
                        .link(String.format("/department/%s/courses/%s/edit", department.getCode(), course.getCode()))
                        .build());
    }

    @GetMapping("/edit")
    public String addCourse(Model model, @PathVariable Department department, @PathVariable Course course) {
        String templateUrl = "department/edit_course";
        if (course == null)
            return templateUrl;

        model.addAttribute("page_description", "Edit course details and manage other settings");
        model.addAttribute("department", department);
        model.addAttribute("page_title", "Edit Course : " + department.getName() + " Department");
        model.addAttribute("page_subtitle", "Course Management");
        model.addAttribute("page_path", getPath(department, course));

        model.addAttribute("course_types", CourseType.values());

        if (!model.containsAttribute("course")) {
            model.addAttribute("course", course);
        }

        model.addAttribute("floated", floatedCourseService.isFloated(course));

        return templateUrl;
    }

    @PostMapping("/edit")
    public String postCourse(@PathVariable Department department, @PathVariable Course course, @RequestParam("course") @Valid Course newCourse, BindingResult result, RedirectAttributes redirectAttributes) {
        String redirectUrl = "redirect:/department/{department}/courses/{original}/edit";
        if (course == null)
            return redirectUrl;

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("course", newCourse);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.course", result);
        } else {
            try {
                newCourse.setDepartment(department);
                courseService.updateCourse(course, newCourse);
                redirectAttributes.addFlashAttribute("course_success", "Course saved successfully!");

                return redirectUrl;
            } catch (UpdateException e) {
                log.warn("Course Save Error", e);
                newCourse.setCode(course.getCode());
                redirectAttributes.addFlashAttribute("course", newCourse);
                redirectAttributes.addFlashAttribute("course_errors", e.getMessage());
            }
        }

        return redirectUrl;
    }

    @GetMapping("/delete")
    public String deleteCourse(@PathVariable Department department, @PathVariable Course course, RedirectAttributes redirectAttributes) {
        if (course == null) {
            log.warn("Course not deletable");
            redirectAttributes.addFlashAttribute("course_error", "No such course exists");
        } else {
            courseService.deleteCourse(course);
            redirectAttributes.addFlashAttribute("course_success", "Course " + course.getCode() + " deleted successfully!");
        }

        return "redirect:/department/{department}/courses?active=true";
    }
}
