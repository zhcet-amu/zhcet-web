package amu.zhcet.core.faculty;

import amu.zhcet.common.utils.SortUtils;
import amu.zhcet.data.course.CourseManagementService;
import amu.zhcet.data.course.incharge.CourseInCharge;
import amu.zhcet.data.course.incharge.CourseInChargeService;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.data.user.faculty.FacultyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    public String facultyCourses(Model model) {
        model.addAttribute("page_title", "Course Management");
        model.addAttribute("page_subtitle", "Faculty Floated Course Management");
        model.addAttribute("page_description", "Manage and upload attendance for currently floated courses");

        facultyService.getLoggedInMember().ifPresent(facultyMember -> {
            List<CourseInCharge> courseInCharges = courseInChargeService.getCourseByFaculty(facultyMember);
            courseInCharges.sort(Comparator.comparing(o -> {
                Integer compared = o.getFloatedCourse().getCourse().getSemester();
                return compared != null ? compared : 0;
            }));
            model.addAttribute("courseInCharges", courseInCharges);
        });

        return "faculty/courses";
    }

    @GetMapping("faculty/courses/{code}/attendance")
    public String attendance(Model model, @PathVariable String code) {
        courseInChargeService.getCourseInCharge(code).ifPresent(courseInCharge -> {
            model.addAttribute("page_title", courseInCharge.getFloatedCourse().getCourse().getCode() + " - " + courseInCharge.getFloatedCourse().getCourse().getTitle());
            model.addAttribute("page_subtitle", "Attendance management for " + courseInCharge.getFloatedCourse().getCourse().getCode());
            model.addAttribute("page_description", "Upload attendance for the floated course");

            List<CourseRegistration> courseRegistrations = courseInChargeService.getCourseRegistrations(courseInCharge);
            List<String> emails = CourseManagementService
                    .getEmailsFromCourseRegistrations(courseRegistrations.stream())
                    .collect(Collectors.toList());
            SortUtils.sortCourseAttendance(courseRegistrations);

            model.addAttribute("incharge", courseInCharge);
            model.addAttribute("courseRegistrations", courseRegistrations);
            model.addAttribute("course", courseInCharge.getFloatedCourse().getCourse());
            model.addAttribute("email_list", emails);
        });

        return "faculty/course_attendance";
    }


}
