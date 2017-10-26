package in.ac.amu.zhcet.controller.department;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.type.CourseType;
import in.ac.amu.zhcet.service.CourseManagementService;
import in.ac.amu.zhcet.utils.UpdateException;
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
public class CourseEditController {

    private final CourseManagementService courseManagementService;

    @Autowired
    public CourseEditController(CourseManagementService courseManagementService) {
        this.courseManagementService = courseManagementService;
    }

    @PreAuthorize("isOfDepartment(#department, #course)")
    @GetMapping("/department/{department}/courses/{course}/edit")
    public String addCourse(Model model, @PathVariable Department department, @PathVariable Course course) {
        model.addAttribute("page_description", "Edit course details and manage other settings");
        model.addAttribute("department", department);
        model.addAttribute("page_title", "Edit Course : " + department.getName() + " Department");
        model.addAttribute("page_subtitle", "Course Management");

        model.addAttribute("course_types", CourseType.values());

        if (!model.containsAttribute("course")) {
            model.addAttribute("course", course);
        }

        model.addAttribute("floated", courseManagementService.isFloated(course));

        return "department/edit_course";
    }

    @PreAuthorize("isOfDepartment(#department, #course)")
    @PostMapping("/department/{department}/courses/{course}/edit")
    public String postCourse(@PathVariable Department department, @PathVariable Course course, @Valid Course courseEdit, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("course", courseEdit);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.course", result);
        } else {
            try {
                course.setDepartment(department);
                courseManagementService.saveCourse(course, courseEdit);
                redirectAttributes.addFlashAttribute("course_success", "Course saved successfully!");

                return "redirect:/department/{department}/courses/{course}/edit";
            } catch (UpdateException e) {
                log.warn("Course Save Error", e);
                courseEdit.setCode(course.getCode());
                redirectAttributes.addFlashAttribute("course", courseEdit);
                redirectAttributes.addFlashAttribute("course_errors", e.getMessage());
            }
        }

        return "redirect:/department/{department}/courses/{course}/edit";
    }

    @PreAuthorize("isOfDepartment(#department, #course)")
    @GetMapping("/department/{department}/courses/{course}/delete")
    public String deleteCourse(@PathVariable Department department, @PathVariable Course course, RedirectAttributes redirectAttributes) {
        if (course == null) {
            log.warn("Course not deletable");
            redirectAttributes.addFlashAttribute("course_error", "No such course exists");
        } else {
            courseManagementService.deleteCourse(course);
            redirectAttributes.addFlashAttribute("course_success", "Course " + course.getCode() + " deleted successfully!");
        }

        return "redirect:/department/{department}/courses?active=true";
    }
}
