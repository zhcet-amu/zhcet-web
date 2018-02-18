package amu.zhcet.core.admin.dean.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/dean/ui")
public class UiController {

    @GetMapping
    public String get(Model model) {
        model.addAttribute("page_title", "UI Management Panel");
        model.addAttribute("page_subtitle", "Dynamic UI management");
        return "dean/ui";
    }

}
