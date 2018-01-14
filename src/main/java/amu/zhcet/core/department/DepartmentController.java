package amu.zhcet.core.department;

import amu.zhcet.common.page.Path;
import amu.zhcet.common.page.PathChain;
import amu.zhcet.core.auth.Auditor;
import amu.zhcet.core.auth.CustomUser;
import amu.zhcet.data.department.Department;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/department")
public class DepartmentController {

    @GetMapping
    public String department() {
        Optional<CustomUser> customUserOptional = Auditor.getLoggedInUser();
        CustomUser user = customUserOptional.orElseThrow(() -> new AccessDeniedException("403"));
        return String.format("redirect:/department/%s", user.getDepartment().getCode());
    }

    public static PathChain getPath(Department department) {
        return PathChain.start()
                .add(Path.builder().title("Departments").build())
                .add(Path.builder().title(department.getName())
                        .link(String.format("/department/%s", department.getCode()))
                        .build());
    }

    @GetMapping("/{department}")
    public String departmentPage(Model model, @PathVariable Department department) {
        String templateUrl = "department/admin";
        Optional.ofNullable(department).ifPresent(dept -> {
            model.addAttribute("page_description", "Manage and float courses for the session");
            model.addAttribute("department", department);
            model.addAttribute("page_subtitle", "Course management for Department");
            model.addAttribute("page_title", department.getName() + " Department Panel");
            model.addAttribute("page_path", getPath(department));
        });

        return templateUrl;
    }

}
