package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class DeanController {
    @GetMapping("/dean-admin")
    public String deanAdmin(Model model){
        model.addAttribute("user", new User());
        return "user";
    }

    @PostMapping("/dean-admin")
    public String enterUser(Model model, @ModelAttribute("user") User user, BindingResult bindingResult, @RequestParam String enrol){
        String role = user.getRoles()[0];
        if(role.equals("ROLE_STUDENT")) {
            Student student = new Student(user, enrol);
        }else if(role.equals("ROLE_FACULTY")){

        }
        return "user";
    }
}
