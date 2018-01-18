package amu.zhcet.core.faculty;

import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.course.incharge.CourseInCharge;
import amu.zhcet.data.course.incharge.CourseInChargeService;
import amu.zhcet.data.user.faculty.FacultyMember;
import amu.zhcet.data.user.faculty.FacultyMemberNotFoundException;
import amu.zhcet.data.user.faculty.FacultyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/faculty/courses")
public class FacultyCourseController {

    private final FacultyService facultyService;
    private final CourseInChargeService courseInChargeService;

    @Autowired
    public FacultyCourseController(FacultyService facultyService, CourseInChargeService courseInChargeService) {
        this.facultyService = facultyService;
        this.courseInChargeService = courseInChargeService;
    }

    @GetMapping
    public String facultyCourses(Model model) {
        model.addAttribute("page_title", "Course Management");
        model.addAttribute("page_subtitle", "Faculty Floated Course Management");
        model.addAttribute("page_description", "Manage and upload attendance for currently floated courses");

        FacultyMember facultyMember = facultyService.getLoggedInMember().orElseThrow(FacultyMemberNotFoundException::new);
        List<CourseInCharge> courseInCharges = courseInChargeService.getCourseByFaculty(facultyMember);

        // Set the no of registrations for each course
        for (CourseInCharge courseInCharge : courseInCharges) {
            FloatedCourse floatedCourse = courseInCharge.getFloatedCourse();
            floatedCourse.getCourse().setRegistrations(floatedCourse.getCourseRegistrations().size());
        }

        // Sort courses by semester
        courseInCharges.sort(Comparator.comparing(o -> {
            Integer compared = o.getFloatedCourse().getCourse().getSemester();
            return compared != null ? compared : 0;
        }));
        model.addAttribute("courseInCharges", courseInCharges);

        return "faculty/courses";
    }

}
