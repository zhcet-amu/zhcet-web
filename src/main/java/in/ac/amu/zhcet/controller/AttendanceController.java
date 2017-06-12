package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.Attendance;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.User;
import in.ac.amu.zhcet.data.repository.AttendanceRepository;
import in.ac.amu.zhcet.data.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AttendanceController {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;

    @Autowired
    public AttendanceController(StudentRepository studentRepository, AttendanceRepository attendanceRepository) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
    }


    @GetMapping("/attendance")
    public String attendance(Model model) {


        model.addAttribute("student", new Student(new User(), null));

        return "attendance";
    }

    @PostMapping(value = "/attendance")
    public String postAttendance(Model model, @RequestParam String fac_no) {

        Student student = studentRepository.getByUser_userId(fac_no);
        List<Attendance> attendances = attendanceRepository.findBySessionAndStudent("A17",student);

        model.addAttribute("student", student);
        model.addAttribute("attendances", attendances);

        return "attendance";
    }

}
