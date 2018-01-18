package amu.zhcet.core.admin.department.course.create;

import amu.zhcet.common.error.DuplicateException;
import amu.zhcet.common.page.Path;
import amu.zhcet.common.page.PathChain;
import amu.zhcet.core.admin.department.course.CoursesController;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/admin/department/{department}/course/add")
public class CourseCreationController {

    private final CourseService courseService;

    @Autowired
    public CourseCreationController(CourseService courseService, FloatedCourseService floatedCourseService) {
        this.courseService = courseService;
    }

    public static PathChain getPath(Department department) {
        return CoursesController.getPath(department)
                .add(Path.builder().title("Create")
                        .link(String.format("/admin/department/%s/course/add", department.getCode()))
                        .build());
    }

    @GetMapping
    public String addCourse(Model model, @PathVariable Department department) {
        ErrorUtils.requireNonNullDepartment(department);

        model.addAttribute("page_description", "Create new global course for the Department");
        model.addAttribute("department", department);
        model.addAttribute("page_title", "Add Course : " + department.getName() + " Department");
        model.addAttribute("page_subtitle", "Course Management");
        model.addAttribute("page_path", getPath(department));

        model.addAttribute("course_types", CourseType.values());

        if (!model.containsAttribute("course")) {
            Course course = new Course();
            course.setDepartment(department);
            model.addAttribute("course", course);
        }

        return "department/add_course";
    }

    @PostMapping
    public String postCourse(@PathVariable Department department, @Valid Course course, BindingResult result, RedirectAttributes redirectAttributes) {
        ErrorUtils.requireNonNullDepartment(department);

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("course", course);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.course", result);
        } else {
            try {
                course.setDepartment(department);
                courseService.addCourse(course);
                redirectAttributes.addFlashAttribute("course_success", "Course created successfully!");

                return "redirect:/admin/department/{department}/courses?active=true";
            } catch (DuplicateException e) {
                log.warn("Duplicate Course", e);
                redirectAttributes.addFlashAttribute("course", course);
                redirectAttributes.addFlashAttribute("course_errors", e.getMessage());
            }
        }

        return "redirect:/admin/department/{department}/course/add";
    }

}
