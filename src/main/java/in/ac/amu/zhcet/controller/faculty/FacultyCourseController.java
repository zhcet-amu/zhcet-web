package in.ac.amu.zhcet.controller.faculty;

import in.ac.amu.zhcet.data.model.CourseInCharge;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.service.CourseInChargeService;
import in.ac.amu.zhcet.service.FacultyService;
import in.ac.amu.zhcet.service.misc.AttendanceDownloadService;
import in.ac.amu.zhcet.utils.Utils;
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

    @GetMapping("faculty/courses/{id}/attendance")
    public String attendance(Model model, @PathVariable String id, @RequestParam(required = false) String section) {
        CourseInCharge courseInCharge = courseInChargeService.getCourseInChargeAndVerify(id, section);

        model.addAttribute("page_title", courseInCharge.getFloatedCourse().getCourse().getTitle());
        model.addAttribute("page_subtitle", "Attendance management for " + courseInCharge.getFloatedCourse().getCourse().getCode());
        model.addAttribute("page_description", "Upload attendance for the floated course");

        List<CourseRegistration> courseRegistrations = courseInChargeService.getCourseRegistrations(courseInCharge);
        Utils.sortCourseAttendance(courseRegistrations);
        model.addAttribute("courseRegistrations", courseRegistrations);
        model.addAttribute("course_id", id);

        return "faculty/course_attendance";
    }

    @GetMapping("faculty/courses/{id}/attendance/download")
    public void getStudents(HttpServletResponse response, @PathVariable String id, @RequestParam(required = false) String section) throws IOException {
        CourseInCharge courseInCharge = courseInChargeService.getCourseInChargeAndVerify(id, section);

        List<CourseRegistration> courseRegistrations = courseInChargeService.getCourseRegistrations(courseInCharge);
        Utils.sortCourseAttendance(courseRegistrations);

        response.setContentType("text/csv");
        response.setHeader("Content-disposition", "attachment;filename=attendance_" + id + "_" + Utils.defaultString(section, "all") + ".csv");

        List<String> lines = attendanceDownloadService.attendanceCsv("faculty", id + "_" + section, courseRegistrations);
        for (String line : lines) {
            response.getOutputStream().println(line);
        }

        response.getOutputStream().flush();
    }
}
