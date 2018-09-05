package amu.zhcet.core.dev;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard/dev")
public class DevelopmentController {

    @GetMapping
    public String devDashboard(Model model) {
        model.addAttribute("page_title", "Development Dashboard");
        model.addAttribute("page_subtitle", "Attendance Panel for Developers");
        model.addAttribute("page_description", "View and manage services as a developer");

        return "dev/dashboard";
    }

}
