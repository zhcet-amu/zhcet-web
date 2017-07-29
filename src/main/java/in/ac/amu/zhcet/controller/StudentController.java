package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.h2.jdbc.JdbcSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/student")
    public String student(Model model) {
        model.addAttribute("student", studentService.getLoggedInStudent());

        return "student";
    }

    @PostMapping("/student")
    public String saveStudent(@ModelAttribute Student student, final RedirectAttributes redirectAttributes) {

        log.info(student.getUserDetails().toString());
        try {
            studentService.updateStudentDetails(student.getEnrolmentNumber(), student.getUserDetails());
            redirectAttributes.addFlashAttribute("success", true);
        } catch (Exception exc) {
            exc.printStackTrace();
            List<String> errors = new ArrayList<>();

            if (exc.getMessage().contains("UK_STUDENT_EMAIL"))
                errors.add("This email address is already used by another person");

            redirectAttributes.addFlashAttribute("errors", errors);
        }

        return "redirect:student";
    }

}
