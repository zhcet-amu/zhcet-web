package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.BaseUser;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.repository.StudentRepository;
import in.ac.amu.zhcet.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class DeanController {

    @GetMapping("/dean")
    public String deanAdmin(Model model) {
        model.addAttribute("user", new BaseUser());
        return "dean";
    }

    @PostMapping("/dean")
    public String enterUser() {
        return "dean";
    }
}
