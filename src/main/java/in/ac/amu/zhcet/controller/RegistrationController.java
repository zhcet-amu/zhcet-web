package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.User;
import in.ac.amu.zhcet.data.repository.StudentRepository;
import in.ac.amu.zhcet.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {
    @Autowired
    private final StudentRepository studentRepository;

    @Autowired
    private final UserRepository userRepository;

    public RegistrationController(StudentRepository studentRepository, UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/signup")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());

        return "signup";
    }

    @PostMapping(value = "/profile")
    public String registerSubmit(Model model, @ModelAttribute User user, @RequestParam String enrolment) {
        user.setRoles(new String[]{"ROLE_STUDENT"});
        user.setActive(true);
        userRepository.save(user);

        Student student = new Student(user, enrolment);
        studentRepository.save(student);

        System.out.print(user.toString());
        model.addAttribute("student", student);
        return "profile";
    }

}
