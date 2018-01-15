package amu.zhcet.core.faculty.attendance;

import amu.zhcet.common.utils.SortUtils;
import amu.zhcet.data.course.floated.FloatedCourseService;
import amu.zhcet.data.course.incharge.CourseInCharge;
import amu.zhcet.data.course.incharge.CourseInChargeNotFoundException;
import amu.zhcet.data.course.incharge.CourseInChargeService;
import amu.zhcet.data.course.registration.CourseRegistration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/faculty/courses/{code}/attendance")
public class FacultyCourseAttendanceController {

    private final CourseInChargeService courseInChargeService;

    @Autowired
    public FacultyCourseAttendanceController(CourseInChargeService courseInChargeService) {
        this.courseInChargeService = courseInChargeService;
    }

    @GetMapping
    public String attendance(Model model, @PathVariable String code) {
        CourseInCharge courseInCharge = courseInChargeService.getCourseInCharge(code).orElseThrow(CourseInChargeNotFoundException::new);

        model.addAttribute("page_title", courseInCharge.getFloatedCourse().getCourse().getCode() + " - " + courseInCharge.getFloatedCourse().getCourse().getTitle());
        model.addAttribute("page_subtitle", "Attendance management for " + courseInCharge.getFloatedCourse().getCourse().getCode());
        model.addAttribute("page_description", "Upload attendance for the floated course");

        List<CourseRegistration> courseRegistrations = courseInChargeService.getCourseRegistrations(courseInCharge);
        List<String> emails = FloatedCourseService
                .getEmailsFromCourseRegistrations(courseRegistrations.stream())
                .collect(Collectors.toList());
        SortUtils.sortCourseAttendance(courseRegistrations);

        model.addAttribute("incharge", courseInCharge);
        model.addAttribute("courseRegistrations", courseRegistrations);
        model.addAttribute("course", courseInCharge.getFloatedCourse().getCourse());
        model.addAttribute("email_list", emails);

        return "faculty/course_attendance";
    }

}
