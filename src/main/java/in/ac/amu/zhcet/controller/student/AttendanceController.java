package in.ac.amu.zhcet.controller.student;

import in.ac.amu.zhcet.service.CourseRegistrationService;
import in.ac.amu.zhcet.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AttendanceController {

    private final StudentService studentService;
    private final CourseRegistrationService courseRegistrationService;

    @Autowired
    public AttendanceController(StudentService studentService, CourseRegistrationService courseRegistrationService) {
        this.studentService = studentService;
        this.courseRegistrationService = courseRegistrationService;
    }

    @GetMapping("/student/attendance")
    public String attendance(Model model) {
        studentService.getLoggedInStudent().ifPresent(student -> {
            model.addAttribute("page_title", "Attendance");
            model.addAttribute("page_subtitle", "Attendance Panel for " + student.getEnrolmentNumber() + " | " + student.getUser().getName());
            model.addAttribute("page_description", "View attendance of floated courses this session");
            model.addAttribute("attendances", courseRegistrationService.getAttendanceByStudent(student.getEnrolmentNumber()));
        });

        return "student/attendance";
    }

}
