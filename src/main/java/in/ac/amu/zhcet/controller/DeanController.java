package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DeanController {
    @GetMapping("/dean-admin")
    public String deanAdmin(Model model){
        model.addAttribute("user", new User());
        return "user";
    }
}
