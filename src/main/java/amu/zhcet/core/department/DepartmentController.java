package amu.zhcet.core.department;

import amu.zhcet.common.page.Path;
import amu.zhcet.common.page.PathChain;
import amu.zhcet.core.auth.Auditor;
import amu.zhcet.core.auth.CustomUser;
import amu.zhcet.core.error.ErrorUtils;
import amu.zhcet.data.department.Department;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/department")
public class DepartmentController {

    public static PathChain getPath(Department department) {
        return PathChain.start()
                .add(Path.builder().title("Departments").build())
                .add(Path.builder().title(department.getName())
                        .link(String.format("/department/%s", department.getCode()))
                        .build());
    }

    @GetMapping
    public String department() {
        return Auditor.getLoggedInUser()
                .map(CustomUser::getDepartment)
                .map(Department::getCode)
                .map(code -> "redirect:/department/" + code)
                .orElseThrow(() -> new AccessDeniedException("403"));
    }

    @GetMapping("/{department}")
    public String departmentPage(Model model, @PathVariable Department department) {
        ErrorUtils.requireNonNullDepartment(department);

        model.addAttribute("page_description", "Manage and float courses for the session");
        model.addAttribute("department", department);
        model.addAttribute("page_subtitle", "Course management for Department");
        model.addAttribute("page_title", department.getName() + " Department Panel");
        model.addAttribute("page_path", getPath(department));

        return "department/admin";
    }

}
