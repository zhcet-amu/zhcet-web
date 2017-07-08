package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String saveStudent(@ModelAttribute Student student) {
        studentService.updateStudentDetails(student.getEnrolmentNumber(), student.getUserDetails());

        return "redirect:student";
    }

}
