package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.service.DepartmentAdminService;
import in.ac.amu.zhcet.data.service.FacultyService;
import in.ac.amu.zhcet.utils.Utils;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class DepartmentController {

    private final DepartmentAdminService departmentAdminService;

    public DepartmentController(DepartmentAdminService departmentAdminService) {
        this.departmentAdminService = departmentAdminService;
    }

    @GetMapping("/department")
    public String department(Model model) {
        model.addAttribute("currentSession", Utils.getCurrentSession());
        model.addAttribute("floatedCourses", departmentAdminService.getFloatedCourses());
        model.addAttribute("courses", departmentAdminService.getAllCourses());
        FacultyMember facultyMember = departmentAdminService.getFacultyMember();
        model.addAttribute("department", FacultyService.getDepartment(facultyMember));
        if (!model.containsAttribute("course")) {
            Course course = new Course();
            course.setDepartment(FacultyService.getDepartment(facultyMember));
            model.addAttribute("course", course);
        }
        model.addAttribute("facultyMembers", departmentAdminService.getAllFacultyMembers());

        // TODO: Show floated courses and option to float course by selecting existing course

        return "department";
    }

    @PostMapping("/department/create_course")
    public String createCourse(@Valid Course course, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors() && !bindingResult.hasFieldErrors("department")) {
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
    public String floatCourse(@RequestParam String courseCode, @RequestParam List<String> faculty, RedirectAttributes redirectAttributes, @RequestParam String session) {
        Course course = departmentAdminService.findCourseByCode(courseCode);

        List<String> errors = new ArrayList<>();
        if (course == null) {
            errors.add("No valid course selected");
        } else {
            try {
                departmentAdminService.floatCourse(course, faculty).setSession(session);
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
