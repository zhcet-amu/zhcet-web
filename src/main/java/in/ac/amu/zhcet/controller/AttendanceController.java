package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.service.RegisteredCourseService;
import in.ac.amu.zhcet.data.service.StudentService;
import in.ac.amu.zhcet.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AttendanceController {

    private final StudentService studentService;
    private final RegisteredCourseService registeredCourseService;

    @Autowired
    public AttendanceController(StudentService studentService, RegisteredCourseService registeredCourseService) {
        this.studentService = studentService;
        this.registeredCourseService = registeredCourseService;
    }


    @GetMapping("/attendance")
    public String attendance(Model model) {
        Student student = studentService.getLoggedInStudent();

        model.addAttribute("student", student);
        model.addAttribute("attendances", registeredCourseService.getAttendanceByStudent(student.getEnrolmentNumber()));
        model.addAttribute("currentSession", Utils.getCurrentSession());
        return "attendance";
    }

}
