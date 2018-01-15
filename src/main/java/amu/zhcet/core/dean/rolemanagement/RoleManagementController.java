package amu.zhcet.core.dean.rolemanagement;

import amu.zhcet.common.flash.Flash;
import amu.zhcet.core.error.ErrorUtils;
import amu.zhcet.data.department.Department;
import amu.zhcet.data.user.Role;
import amu.zhcet.data.user.User;
import amu.zhcet.data.user.faculty.FacultyMember;
import amu.zhcet.data.user.faculty.FacultyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/dean/roles")
public class RoleManagementController {

    private final RoleManagementService roleManagementService;
    private final FacultyService facultyService;

    @Autowired
    public RoleManagementController(RoleManagementService roleManagementService, FacultyService facultyService) {
        this.roleManagementService = roleManagementService;
        this.facultyService = facultyService;
    }

    @GetMapping("/department/{department}")
    public String roleManagement(Model model, @PathVariable Department department) {
        ErrorUtils.requireNonNullDepartment(department);

        model.addAttribute("page_title", "Role Management");
        model.addAttribute("page_subtitle", "Role Management Panel for " + department.getName() + " Department");
        model.addAttribute("page_description", "Manage Faculty Roles and Permissions");

        model.addAttribute("department", department);
        List<FacultyMember> facultyMembers = facultyService.getByDepartment(department);
        // Sort Faculty Members according to created date ignoring null
        facultyMembers.sort((f1, f2) -> {
            if (f1.getCreatedAt() != null && f2.getCreatedAt() != null)
                return f1.getCreatedAt().compareTo(f2.getCreatedAt());

            return (f1.getCreatedAt() == null) ? -1 : 1;
        });
        model.addAttribute("facultyMembers", facultyMembers);

        return "dean/role_management";
    }

    @GetMapping("/user/{user}")
    public String rolePage(Model model, @PathVariable User user) {
        ErrorUtils.requireNonNullUser(user);

        model.addAttribute("page_title", "Role Management");
        model.addAttribute("page_subtitle", "Role Management Panel for " + user.getName());
        model.addAttribute("page_description", "Manage Faculty Roles and Permissions");

        model.addAttribute("user", user);
        List<Role> roles = new ArrayList<>(Arrays.asList(Role.values()));
        // Remove unimplemented roles
        roles.remove(Role.TEACHING_STAFF);
        roles.remove(Role.SUPER_FACULTY);
        roles.sort(Comparator.comparingInt(Role::getOrder));
        model.addAttribute("roles", roles);

        Map<String, List<String>> roleHierarchy = roleManagementService.getRoleHierarchyMap();
        model.addAttribute("roleHierarchy", roleHierarchy);

        Map<String, Integer> roleOrder = Arrays.stream(Role.values())
                .collect(Collectors.toMap(Role::toString, Role::getOrder));
        model.addAttribute("roleOrder", roleOrder);

        return "dean/role_management_page";
    }

    @PostMapping("/user/{user}")
    public String postRoles(RedirectAttributes redirectAttributes, @PathVariable User user, @RequestParam(required = false) List<String> roles) {
        ErrorUtils.requireNonNullUser(user);

        roleManagementService.saveRoles(user, roles);

        redirectAttributes.addFlashAttribute("flash_messages",
                Flash.title("Saved!").success("Roles have been saved"));

        return "redirect:/dean/roles/user/{user}";
    }

}
