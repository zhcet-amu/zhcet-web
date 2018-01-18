package amu.zhcet.core.student;

import amu.zhcet.data.attendance.AttendanceService;
import amu.zhcet.data.user.student.Student;
import amu.zhcet.data.user.student.StudentNotFoundException;
import amu.zhcet.data.user.student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard/student/attendance")
public class StudentAttendanceController {

    private final StudentService studentService;
    private final AttendanceService attendanceService;

    @Autowired
    public StudentAttendanceController(StudentService studentService, AttendanceService attendanceService) {
        this.studentService = studentService;
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public String attendance(Model model) {
        Student student = studentService.getLoggedInStudent().orElseThrow(StudentNotFoundException::new);
        model.addAttribute("page_title", "Attendance");
        model.addAttribute("page_subtitle", "Attendance Panel for " + student.getEnrolmentNumber() + " | " + student.getUser().getName());
        model.addAttribute("page_description", "View attendance of floated courses this session");
        model.addAttribute("attendances", attendanceService.getAttendanceByStudent(student));

        return "student/attendance";
    }

}
