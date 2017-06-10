package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AttendanceController {

    @GetMapping("/attendance")
    public String attendance(Model model) {
        model.addAttribute("student", new Student());

        return "attendance";
    }

    @PostMapping(value = "/attendance")
    public String postAttendance(Model model, @RequestParam String fac_no) {

        StudentRepository studentRepository = new StudentRepository();

        Student student = studentRepository.getStudentById(fac_no);

        model.addAttribute("student", student);
        model.addAttribute("userId", fac_no);

        return "attendance";
    }

}
