package in.ac.amu.zhcet.controller.department;

import in.ac.amu.zhcet.data.CourseType;
import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.service.core.DepartmentAdminService;
import in.ac.amu.zhcet.service.core.FacultyService;
import in.ac.amu.zhcet.utils.DuplicateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class CourseCreationController {

    private final DepartmentAdminService departmentAdminService;

    public CourseCreationController(DepartmentAdminService departmentAdminService) {
        this.departmentAdminService = departmentAdminService;
    }

    @GetMapping("/department/course/add")
    public String addCourse(Model model) {
        model.addAttribute("page_description", "Create new global course for the Department");
        FacultyMember facultyMember = departmentAdminService.getFacultyMember();
        Department department = FacultyService.getDepartment(facultyMember);
        model.addAttribute("department", department);
        model.addAttribute("page_title", department.getName() + " Department : Add Course");
        model.addAttribute("page_subtitle", "Course Management");

        model.addAttribute("form_title", "Add Course");
        model.addAttribute("course_types", CourseType.values());

        if (!model.containsAttribute("course")) {
            Course course = new Course();
            course.setDepartment(FacultyService.getDepartment(facultyMember));
            model.addAttribute("course", course);
        }

        return "department/add_course";
    }

    @PostMapping("/department/course/add")
    public String postCourse(@Valid Course course, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("course", course);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.course", result);
        } else {
            try {
                course.setDepartment(departmentAdminService.getFacultyMember().getUser().getDepartment());
                departmentAdminService.addCourse(course);
                redirectAttributes.addFlashAttribute("course_success", true);

                return "redirect:/department";
            } catch (DuplicateException e) {
                redirectAttributes.addFlashAttribute("course", course);
                redirectAttributes.addFlashAttribute("course_errors", e.getMessage());
            }
        }

        return "redirect:/department/course/add";
    }

    @PostMapping("/department/create_course")
    public String createCourse(@Valid Course course, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors() && !bindingResult.hasFieldErrors("department")) {
            redirectAttributes.addFlashAttribute("course", course);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.course", bindingResult);
        } else {
            try {
                course.setDepartment(departmentAdminService.getFacultyMember().getUser().getDepartment());
                departmentAdminService.addCourse(course);
                redirectAttributes.addFlashAttribute("course_success", true);
            } catch (Exception e) {
                List<String> errors = new ArrayList<>();
                if (e.getMessage().contains("PRIMARY")) {
                    errors.add("Course with this code already exists");
                }

                redirectAttributes.addFlashAttribute("course", course);
                redirectAttributes.addFlashAttribute("course_errors", errors);
            }
        }

        return "redirect:/department";
    }

    @PostMapping("/department/float_course")
    public String floatCourse(@RequestParam String courseCode, @RequestParam List<String> faculty, RedirectAttributes redirectAttributes) {
        Course course = departmentAdminService.findCourseByCode(courseCode);

        List<String> errors = new ArrayList<>();
        if (course == null) {
            errors.add("No valid course selected");
        } else {
            try {
                departmentAdminService.floatCourse(course, faculty);
                redirectAttributes.addFlashAttribute("float_success", true);
            } catch (Exception exc) {
                if (exc.getMessage().contains("PRIMARY")) {
                    errors.add("This course is already floated!");
                }
            }
        }

        redirectAttributes.addFlashAttribute("float_errors", errors);

        return "redirect:/department";
    }

}
