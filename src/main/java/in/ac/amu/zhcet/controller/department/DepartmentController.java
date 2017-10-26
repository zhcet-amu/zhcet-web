package in.ac.amu.zhcet.controller.department;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.service.user.Auditor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
public class DepartmentController {

    @GetMapping("/department")
    public String department() {
        return "redirect:/department/" + Auditor.getLoggedInUser().getDepartment().getCode();
    }

    @PreAuthorize("isDepartment(#department)")
    @GetMapping("/department/{department}")
    public String departmentPage(Model model, @PathVariable Department department) {
        model.addAttribute("page_description", "Manage and float courses for the session");
        model.addAttribute("department", department);
        model.addAttribute("page_title", department.getName() + " Department Panel");
        model.addAttribute("page_subtitle", "Course management for Department");

        return "department/admin";
    }

}
