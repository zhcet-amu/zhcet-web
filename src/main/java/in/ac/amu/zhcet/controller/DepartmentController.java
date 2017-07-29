package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.repository.CourseRepository;
import in.ac.amu.zhcet.data.service.DepartmentAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class DepartmentController {

    private final DepartmentAdminService departmentAdminService;
    private final CourseRepository courseRepository;

    public DepartmentController(DepartmentAdminService departmentAdminService, CourseRepository courseRepository) {
        this.departmentAdminService = departmentAdminService;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/department")
    public String department(Model model) {
        model.addAttribute("floatedCourses", departmentAdminService.getFloatedCourses());
        model.addAttribute("courses", departmentAdminService.getAllCourses());
        FacultyMember facultyMember = departmentAdminService.getFacultyMember();
        model.addAttribute("department", facultyMember.getDepartment());
        if (!model.containsAttribute("course")) {
            Course course = new Course();
            course.setDepartment(facultyMember.getDepartment());
            model.addAttribute("course", course);
        }
        model.addAttribute("facultyMembers", departmentAdminService.getAllFacultyMembers());

        // TODO: Show floated courses and option to float course by selecting existing course

        return "department";
    }

    @PostMapping("/department/create_course")
    public String createCourse(@Valid Course course, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("course", course);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.course", bindingResult);
        } else {
            try {
                departmentAdminService.registerCourse(course);
                redirectAttributes.addFlashAttribute("course_success", true);
            } catch (Exception e) {
                List<String> errors = new ArrayList<>();
                if (e instanceof DataIntegrityViolationException && e.getMessage().contains("PRIMARY_KEY")) {
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
                if (exc instanceof DataIntegrityViolationException && exc.getMessage().contains("PRIMARY_KEY")) {
                    errors.add("This course is already floated!");
                } else {
                    errors.add(exc.getMessage());
                }
            }
        }

        redirectAttributes.addFlashAttribute("float_errors", errors);

        return "redirect:/department";
    }

}
