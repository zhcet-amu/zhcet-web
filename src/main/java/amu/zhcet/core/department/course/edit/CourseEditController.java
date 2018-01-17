package amu.zhcet.core.department.course.edit;

import amu.zhcet.common.error.UpdateException;
import amu.zhcet.common.page.Path;
import amu.zhcet.common.page.PathChain;
import amu.zhcet.core.department.course.CoursesController;
import amu.zhcet.core.error.ErrorUtils;
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
@RequestMapping("/admin/department/courses/{course}")
public class CourseEditController {

    private final CourseService courseService;
    private final FloatedCourseService floatedCourseService;

    @Autowired
    public CourseEditController(CourseService courseService, FloatedCourseService floatedCourseService) {
        this.courseService = courseService;
        this.floatedCourseService = floatedCourseService;
    }

    public static PathChain getPath(Course course) {
        return CoursesController.getPath(course.getDepartment())
                .add(Path.builder().title(course.getCode())
                        .build())
                .add(Path.builder().title("Edit")
                        .link(String.format("/admin/department/courses/%s/edit", course.getCode()))
                        .build());
    }

    @GetMapping("/edit")
    public String addCourse(Model model, @PathVariable Course course) {
        ErrorUtils.requireNonNullCourse(course);

        Department department = course.getDepartment();
        model.addAttribute("page_description", "Edit course details and manage other settings");
        model.addAttribute("department", department);
        model.addAttribute("page_title", "Edit Course : " + department.getName() + " Department");
        model.addAttribute("page_subtitle", "Course Management");
        model.addAttribute("page_path", getPath(course));

        model.addAttribute("course_types", CourseType.values());

        if (!model.containsAttribute("course")) {
            model.addAttribute("course", course);
        }

        model.addAttribute("floated", floatedCourseService.isFloated(course));

        return "department/edit_course";
    }

    @PostMapping("/edit")
    public String postCourse(@PathVariable Course course, @ModelAttribute("course") @Valid Course newCourse, BindingResult result, RedirectAttributes redirectAttributes) {
        ErrorUtils.requireNonNullCourse(course);

        Department department = course.getDepartment();
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("course", newCourse);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.course", result);
        } else {
            try {
                newCourse.setDepartment(department);
                courseService.updateCourse(course, newCourse);
                redirectAttributes.addFlashAttribute("course_success", "Course saved successfully!");
            } catch (UpdateException e) {
                log.warn("Course Save Error", e);
                newCourse.setCode(course.getCode());
                redirectAttributes.addFlashAttribute("course", newCourse);
                redirectAttributes.addFlashAttribute("course_errors", e.getMessage());
            }
        }

        return "redirect:/admin/department/courses/{course}/edit";
    }

    @GetMapping("/delete")
    public String deleteCourse(@PathVariable Course course, RedirectAttributes redirectAttributes) {
        ErrorUtils.requireNonNullCourse(course);

        courseService.deleteCourse(course);
        redirectAttributes.addFlashAttribute("course_success", "Course " + course.getCode() + " deleted successfully!");
        redirectAttributes.addAttribute("department", course.getDepartment());

        return "redirect:/admin/department/{department}/courses?active=true";
    }
}
