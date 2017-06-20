package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.User;
import in.ac.amu.zhcet.data.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Base64;

@Controller
public class LoginController {

    private final StudentRepository studentRepository;

    @Autowired
    public LoginController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "login";
    }

    @PostMapping("/login")
    public String login(Model model, @ModelAttribute User user, @RequestParam String password) {
        Student student = studentRepository.getByUser_userId(user.getUserId());
        if (user.PASSWORD_ENCODER.matches(password, student.getUser().getPassword())) {
            model.addAttribute("student", student);
            System.out.print("hello   " + student.toString());
            return "profile";
        }
        model.addAttribute("error", "There seems an error");
        return "login";
    }
}
