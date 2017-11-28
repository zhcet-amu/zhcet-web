package in.ac.amu.zhcet.controller.dean;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.service.DepartmentService;
import in.ac.amu.zhcet.service.UserService;
import in.ac.amu.zhcet.utils.exception.DuplicateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class DeanController {

    private final UserService userService;
    private final DepartmentService departmentService;

    @Autowired
    public DeanController(UserService userService, DepartmentService departmentService) {
        this.userService = userService;
        this.departmentService = departmentService;
    }

    @GetMapping("/dean")
    public String deanAdmin(Model model) {
        model.addAttribute("page_title", "Administration Panel");
        model.addAttribute("page_subtitle", "Dean Administration Panel");
        model.addAttribute("page_description", "Register Students and Faculty, manage roles and users");

        model.addAttribute("users", userService.getAll());
        if (!model.containsAttribute("department"))
            model.addAttribute("department", new Department());
        model.addAttribute("departments", departmentService.findAll());

        return "dean/admin";
    }

    @PostMapping("/dean/departments/add")
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

        return "redirect:/dean";
    }

}
