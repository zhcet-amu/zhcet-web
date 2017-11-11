package in.ac.amu.zhcet.controller.faculty;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.CourseInCharge;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.service.CourseInChargeService;
import in.ac.amu.zhcet.service.CourseManagementService;
import in.ac.amu.zhcet.service.FacultyService;
import in.ac.amu.zhcet.service.extra.AttendanceDownloadService;
import in.ac.amu.zhcet.utils.SortUtils;
import in.ac.amu.zhcet.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class FacultyCourseController {

    private final FacultyService facultyService;
    private final CourseInChargeService courseInChargeService;
    private final AttendanceDownloadService attendanceDownloadService;

    @Autowired
    public FacultyCourseController(AttendanceDownloadService attendanceDownloadService, FacultyService facultyService, CourseInChargeService courseInChargeService) {
        this.attendanceDownloadService = attendanceDownloadService;
        this.facultyService = facultyService;
        this.courseInChargeService = courseInChargeService;
    }

    @GetMapping("/faculty/courses")
    public String facultyCourses(Model model) {
        model.addAttribute("page_title", "Course Management");
        model.addAttribute("page_subtitle", "Faculty Floated Course Management");
        model.addAttribute("page_description", "Manage and upload attendance for currently floated courses");

        FacultyMember facultyMember = facultyService.getLoggedInMember();
        List<CourseInCharge> courseInCharges = courseInChargeService.getCourseByFaculty(facultyMember);
        courseInCharges.sort(Comparator.comparing(o -> {
            Integer compared = o.getFloatedCourse().getCourse().getSemester();
            return compared != null ? compared : 0;
        }));
        model.addAttribute("courseInCharges", courseInCharges);
        return "faculty/courses";
    }

    @GetMapping("faculty/courses/{course}/attendance")
    public String attendance(Model model, @PathVariable Course course, @RequestParam(required = false) String section) {
        CourseInCharge courseInCharge = courseInChargeService.getCourseInCharge(course, section);

        model.addAttribute("page_title", course.getCode() + " - " + course.getTitle());
        model.addAttribute("page_subtitle", "Attendance management for " + courseInCharge.getFloatedCourse().getCourse().getCode());
        model.addAttribute("page_description", "Upload attendance for the floated course");

        List<CourseRegistration> courseRegistrations = courseInChargeService.getCourseRegistrations(courseInCharge);
        List<String> emails = CourseManagementService
                .getEmailsFromCourseRegistrations(courseRegistrations.stream())
                .collect(Collectors.toList());
        SortUtils.sortCourseAttendance(courseRegistrations);

        model.addAttribute("courseRegistrations", courseRegistrations);
        model.addAttribute("course", course);
        model.addAttribute("email_list", emails);

        return "faculty/course_attendance";
    }

    @GetMapping("faculty/courses/{course}/attendance/download")
    public void getStudents(HttpServletResponse response, @PathVariable Course course, @RequestParam(required = false) String section) throws IOException {
        CourseInCharge courseInCharge = courseInChargeService.getCourseInCharge(course, section);
        List<CourseRegistration> courseRegistrations = courseInChargeService.getCourseRegistrations(courseInCharge);
        attendanceDownloadService.download(course.getCode() + "_" +
                StringUtils.defaultString(section, "all"), "faculty", courseRegistrations, response);
    }
}
