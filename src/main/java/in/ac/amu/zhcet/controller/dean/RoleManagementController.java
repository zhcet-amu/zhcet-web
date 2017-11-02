package in.ac.amu.zhcet.controller.dean;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.data.type.Roles;
import in.ac.amu.zhcet.service.FacultyService;
import in.ac.amu.zhcet.service.user.UserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Controller
public class RoleManagementController {

    private final UserDetailService userDetailService;
    private final FacultyService facultyService;

    @Autowired
    public RoleManagementController(UserDetailService userDetailService, FacultyService facultyService) {
        this.userDetailService = userDetailService;
        this.facultyService = facultyService;
    }

    @PreAuthorize("isDepartment(#department)")
    @GetMapping("/dean/roles/{department}")
    public String roleManagement(Model model, @PathVariable Department department) {

        if (department != null) {
            model.addAttribute("page_title", "Role Management");
            model.addAttribute("page_subtitle", "Role Management Panel for " + department.getName() + " Department");
            model.addAttribute("page_description", "Manage Faculty Roles and Permissions");

            model.addAttribute("department", department);
            model.addAttribute("facultyMembers", facultyService.getAllByDepartment(department));
        }

        return "dean/role_management";
    }

    @PreAuthorize("isDepartment(#department)")
    @PostMapping("/dean/roles/{department}")
    public String saveRoles(RedirectAttributes redirectAttributes, @PathVariable Department department, @RequestParam String facultyId, @RequestParam(required = false) List<String> roles) {
        FacultyMember facultyMember = facultyService.getById(facultyId);

        if (facultyMember == null)
            return "redirect:/dean/roles/{department}";

        Set<String> newRoles = new HashSet<>();

        if (roles != null)
        for (String role : roles) {
            switch (role) {
                case "dean":
                    newRoles.add(Roles.DEAN_ADMIN);
                    break;
                case "department_super":
                    newRoles.add(Roles.DEPARTMENT_SUPER_ADMIN);
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

        facultyMember.getUser().setRoles(newRoles);
        facultyService.save(facultyMember);

        UserAuth loggedIn = userDetailService.getLoggedInUser();
        if (facultyMember.getUser().getUserId().equals(loggedIn.getUserId()))
            userDetailService.updatePrincipal(loggedIn);

        redirectAttributes.addFlashAttribute("saved", true);

        return "redirect:/dean/roles/{department}";
    }

}
