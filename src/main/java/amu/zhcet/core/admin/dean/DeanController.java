package amu.zhcet.core.admin.dean;

import amu.zhcet.common.error.DuplicateException;
import amu.zhcet.core.admin.dean.registration.faculty.FacultyRegistrationController;
import amu.zhcet.core.admin.dean.registration.student.StudentRegistrationController;
import amu.zhcet.data.department.Department;
import amu.zhcet.data.department.DepartmentService;
import amu.zhcet.data.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/dean")
public class DeanController {

    private final UserService userService;
    private final DepartmentService departmentService;

    @Autowired
    public DeanController(UserService userService, DepartmentService departmentService) {
        this.userService = userService;
        this.departmentService = departmentService;
    }

    @GetMapping
    public String deanAdmin(Model model, WebRequest webRequest) {
        model.addAttribute("page_title", "Administration Panel");
        model.addAttribute("page_subtitle", "Dean Administration Panel");
        model.addAttribute("page_description", "Register Students and Faculty, manage roles and users");

        if (!model.containsAttribute("faculty_success"))
            webRequest.removeAttribute(FacultyRegistrationController.KEY_FACULTY_REGISTRATION,
                    RequestAttributes.SCOPE_SESSION);

        if (!model.containsAttribute("students_success"))
            webRequest.removeAttribute(StudentRegistrationController.KEY_STUDENT_REGISTRATION,
                    RequestAttributes.SCOPE_SESSION);

        model.addAttribute("users", userService.getAll());
        if (!model.containsAttribute("department"))
            model.addAttribute("department", new Department());
        model.addAttribute("departments", departmentService.findAll());

        return "dean/admin";
    }

    @PostMapping("/departments/add")
    public String addDepartment(@Valid Department department, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.department", bindingResult);
            redirectAttributes.addFlashAttribute("department", department);
        } else {
            try {
                departmentService.addDepartment(department);
                redirectAttributes.addFlashAttribute("dept_success", true);
            } catch (DuplicateException de) {
                log.warn("Duplicate Department", de);
                List<String> errors = new ArrayList<>();
                errors.add(de.getMessage());

                redirectAttributes.addFlashAttribute("department", department);
                redirectAttributes.addFlashAttribute("dept_errors", errors);
            }
        }

        return "redirect:/admin/dean";
    }

}
