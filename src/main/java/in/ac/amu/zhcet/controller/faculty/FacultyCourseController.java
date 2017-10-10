package in.ac.amu.zhcet.controller.faculty;

import in.ac.amu.zhcet.data.model.CourseInCharge;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.service.core.CourseInChargeService;
import in.ac.amu.zhcet.service.core.FacultyService;
import in.ac.amu.zhcet.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
public class FacultyCourseController {
    private final FacultyService facultyService;
    private final CourseInChargeService courseInChargeService;

    @Autowired
    public FacultyCourseController(FacultyService facultyService, CourseInChargeService courseInChargeService) {
        this.facultyService = facultyService;
        this.courseInChargeService = courseInChargeService;
    }

    @GetMapping("/faculty/courses")
    public String getCourse(Model model) {
        model.addAttribute("page_title", "Course Management");
        model.addAttribute("page_subtitle", "Faculty Floated Course Management");
        model.addAttribute("page_description", "Manage and upload attendance for currently floated courses");

        FacultyMember facultyMember = facultyService.getLoggedInMember();
        List<CourseInCharge> courseInCharges = courseInChargeService.getCourseByFaculty(facultyMember);
        model.addAttribute("courseInCharges", courseInCharges);
        return "faculty/courses";
    }

    @GetMapping("faculty/courses/{id}")
    public String getStudents(Model model, @PathVariable String id, @RequestParam(required = false) String section) {
        CourseInCharge courseInCharge = courseInChargeService.getCourseInChargeAndVerify(id, section);

        model.addAttribute("page_title", courseInCharge.getFloatedCourse().getCourse().getTitle());
        model.addAttribute("page_subtitle", "Attendance management for " + courseInCharge.getFloatedCourse().getCourse().getCode());
        model.addAttribute("page_description", "Upload attendance for the floated course");

        List<CourseRegistration> courseRegistrations = courseInChargeService.getCourseRegistrations(courseInCharge);
        Utils.sortAttendance(courseRegistrations);
        model.addAttribute("courseRegistrations", courseRegistrations);
        model.addAttribute("course_id", id);

        return "faculty/course_attendance";
    }
}
