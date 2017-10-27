package in.ac.amu.zhcet.controller.department;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.type.CourseType;
import in.ac.amu.zhcet.service.CourseManagementService;
import in.ac.amu.zhcet.utils.DuplicateException;
import in.ac.amu.zhcet.utils.page.Path;
import in.ac.amu.zhcet.utils.page.PathChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Slf4j
@Controller
public class CourseCreationController {

    private final CourseManagementService courseManagementService;

    @Autowired
    public CourseCreationController(CourseManagementService courseManagementService) {
        this.courseManagementService = courseManagementService;
    }

    public static PathChain getPath(Department department) {
        return CoursesController.getPath(department)
                .add(Path.builder().title("Create")
                        .link(String.format("/department/%s/course/add", department.getCode()))
                        .build());
    }

    @PreAuthorize("isDepartment(#department)")
    @GetMapping("/department/{department}/course/add")
    public String addCourse(Model model, @PathVariable Department department) {
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

    @PreAuthorize("isOfDepartment(#department, #course)")
    @PostMapping("/department/{department}/course/add")
    public String postCourse(@PathVariable Department department, @Valid Course course, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("course", course);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.course", result);
        } else {
            try {
                course.setDepartment(department);
                courseManagementService.addCourse(course);
                redirectAttributes.addFlashAttribute("course_success", "Course created successfully!");

                return "redirect:/department/{department}/courses?active=true";
            } catch (DuplicateException e) {
                log.warn("Duplicate Course", e);
                redirectAttributes.addFlashAttribute("course", course);
                redirectAttributes.addFlashAttribute("course_errors", e.getMessage());
            }
        }

        return "redirect:/department/{department}/course/add";
    }

}
