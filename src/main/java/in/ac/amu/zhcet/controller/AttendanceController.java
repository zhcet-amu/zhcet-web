package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.repository.AttendanceRepository;
import in.ac.amu.zhcet.data.service.StudentService;
import in.ac.amu.zhcet.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AttendanceController {

    private final StudentService studentService;
    private final AttendanceRepository attendanceRepository;

    @Autowired
    public AttendanceController(StudentService studentService, AttendanceRepository attendanceRepository) {
        this.studentService = studentService;
        this.attendanceRepository = attendanceRepository;
    }


    @GetMapping("/attendance")
    public String attendance(Model model) {
        Student student = studentService.getLoggedInStudent();

        model.addAttribute("student", student);
        model.addAttribute("attendances", attendanceRepository.findBySessionAndStudent(Utils.getCurrentSession(), student));

        return "attendance";
    }

}
