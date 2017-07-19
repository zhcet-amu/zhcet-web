package in.ac.amu.zhcet.controller;
import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.service.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class FacultyController {
    private final FacultyService facultyService;

    @Autowired
    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("/faculty/courses")
    public String getCourse(Model model){
        FacultyMember facultyMember = facultyService.getLoggedInMember();
        List<FloatedCourse> floatedCourses = facultyService.getByFaculty(facultyMember);
        model.addAttribute("floatedCourses", floatedCourses);
        return "floatedCourse";
    }
}
