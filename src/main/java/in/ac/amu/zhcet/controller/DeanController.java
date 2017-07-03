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

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public DeanController(UserRepository userRepository, StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/dean")
    public String deanAdmin(Model model) {
        model.addAttribute("user", new BaseUser());
        return "user";
    }

    @PostMapping("/dean")
    public String enterUser(Model model, @ModelAttribute("user") BaseUser user, BindingResult bindingResult, @RequestParam String enrol) {
        user.setActive(true);
        userRepository.save(user);
        String role = user.getRoles()[0];
        if (role.equals("ROLE_STUDENT")) {
            Student student = new Student(user, enrol);
            studentRepository.save(student);
        } else if (role.equals("ROLE_FACULTY")) {

        }
        return "user";
    }
}
