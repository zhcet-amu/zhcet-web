package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.base.user.UserDetail;
import in.ac.amu.zhcet.data.service.StudentService;
import in.ac.amu.zhcet.data.service.UserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class StudentController {

    private final StudentService studentService;
    private final UserDetailService userDetailService;

    @Autowired
    public StudentController(StudentService studentService, UserDetailService userDetailService) {
        this.studentService = studentService;
        this.userDetailService = userDetailService;
    }

    @GetMapping("/student")
    public String student(Model model) {
        Student student = studentService.getLoggedInStudent();
        model.addAttribute("student", student);
        model.addAttribute("user_details", student.getUser().getDetails());

        return "student";
    }

    @PostMapping("/student/details")
    public String saveStudent(@ModelAttribute UserDetail userDetail, final RedirectAttributes redirectAttributes) {
        log.info(userDetail.toString());

        try {
            userDetailService.updateDetails(studentService.getLoggedInStudent().getUser(), userDetail);
            redirectAttributes.addFlashAttribute("success", true);
        } catch (Exception exc) {
            exc.printStackTrace();
            List<String> errors = new ArrayList<>();

            if (exc.getMessage().contains("UK_STUDENT_EMAIL"))
                errors.add("This email address is already used by another person");

            redirectAttributes.addFlashAttribute("errors", errors);
        }

        return "redirect:/student";
    }

}
