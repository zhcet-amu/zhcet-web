package in.ac.amu.zhcet.controller.dean;

import in.ac.amu.zhcet.data.Roles;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.service.DepartmentService;
import in.ac.amu.zhcet.data.service.FacultyService;
import in.ac.amu.zhcet.data.service.UserService;
import in.ac.amu.zhcet.utils.DuplicateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Controller
public class DeanController {

    private final UserService userService;
    private final DepartmentService departmentService;
    private final FacultyService facultyService;

    @Autowired
    public DeanController(UserService userService, FacultyService facultyService, DepartmentService departmentService) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.facultyService = facultyService;
    }

    @GetMapping("/dean")
    public String deanAdmin(Model model) {
        model.addAttribute("users", userService.getAll());

        if (!model.containsAttribute("department")) {
            model.addAttribute("department", new Department());
        }

        model.addAttribute("departments", departmentService.findAll());

        return "dean";
    }

    @GetMapping("/dean/roles/{id}")
    public String roleManagement(Model model, @PathVariable long id) {
        Department department = departmentService.findOne(id);

        model.addAttribute("department", department);
        model.addAttribute("facultyMembers", facultyService.getByDepartment(department));

        return "role_management";
    }

    @PostMapping("/dean/roles/{id}/save")
    public String saveRoles(Model model, @PathVariable long id, RedirectAttributes redirectAttributes, @RequestParam String facultyId, @RequestParam List<String> roles) {
        FacultyMember facultyMember = facultyService.getById(facultyId);

        List<String> newRoles = new ArrayList<>();

        for (String role : roles) {
            switch (role) {
                case "dean":
                    newRoles.add(Roles.DEAN_ADMIN);
                    break;
                case "department":
                    newRoles.add(Roles.DEPARTMENT_ADMIN);
                    break;
                case "faculty":
                    newRoles.add(Roles.FACULTY);
                    break;
                default:
                    // Skip
            }
        }

        facultyMember.getUser().setRoles(newRoles.toArray(new String[newRoles.size()]));
        facultyService.save(facultyMember);

        redirectAttributes.addFlashAttribute("saved", true);

        return "redirect:/dean/roles/{id}";
    }

    @PostMapping("/dean/add_department")
    public String addDepartment(@Valid Department department, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.department", bindingResult);
            redirectAttributes.addFlashAttribute("department", department);
        } else {
            try {
                departmentService.addDepartment(department);
                redirectAttributes.addFlashAttribute("dept_success", true);
            } catch (DuplicateException de) {
                List<String> errors = new ArrayList<>();
                errors.add(de.getMessage());

                redirectAttributes.addFlashAttribute("department", department);
                redirectAttributes.addFlashAttribute("dept_errors", errors);
            }
        }

        return "redirect:/dean";
    }


}
