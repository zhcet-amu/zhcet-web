package in.ac.amu.zhcet.controller.department;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.service.user.Auditor;
import in.ac.amu.zhcet.utils.page.Path;
import in.ac.amu.zhcet.utils.page.PathChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
public class DepartmentController {

    @GetMapping("/department")
    public String department() {
        return String.format("redirect:/department/%s", Auditor.getLoggedInUser().getDepartment().getCode());
    }

    public static PathChain getPath(Department department) {
        return PathChain.start()
                .add(Path.builder().title("Departments").build())
                .add(Path.builder().title(department.getName())
                        .link(String.format("/department/%s", department.getCode()))
                        .build());
    }

    @GetMapping("/department/{department}")
    public String departmentPage(Model model, @PathVariable Department department) {
        String templateUrl = "department/admin";
        if (department == null)
            return templateUrl;

        model.addAttribute("page_description", "Manage and float courses for the session");
        model.addAttribute("department", department);
        model.addAttribute("page_subtitle", "Course management for Department");
        model.addAttribute("page_title", department.getName() + " Department Panel");
        model.addAttribute("page_path", getPath(department));

        return templateUrl;
    }

}
