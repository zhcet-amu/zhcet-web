package in.ac.amu.zhcet.controller.dean;

import in.ac.amu.zhcet.data.Roles;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.service.core.DepartmentService;
import in.ac.amu.zhcet.service.core.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class RoleManagementController {

    private final DepartmentService departmentService;
    private final FacultyService facultyService;

    @Autowired
    public RoleManagementController(DepartmentService departmentService, FacultyService facultyService) {
        this.departmentService = departmentService;
        this.facultyService = facultyService;
    }

    @GetMapping("/dean/roles/{id}")
    public String roleManagement(Model model, @PathVariable long id) {
        Department department = departmentService.findOne(id);

        if (department != null) {
            model.addAttribute("page_title", "Role Management");
            model.addAttribute("page_subtitle", "Role Management Panel for " + department.getName() + " Department");
            model.addAttribute("page_description", "Manage Faculty Roles and Permissions");

            model.addAttribute("department", department);
            model.addAttribute("facultyMembers", facultyService.getByDepartment(department));
        }

        return "dean/role_management";
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

}
