package in.ac.amu.zhcet.controller.dean;

import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.service.CourseManagementService;
import in.ac.amu.zhcet.service.misc.AttendanceDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class FloatedCourseEditController {

    private final CourseManagementService courseManagementService;
    private final AttendanceDownloadService attendanceDownloadService;

    @Autowired
    public FloatedCourseEditController(CourseManagementService courseManagementService, AttendanceDownloadService attendanceDownloadService) {
        this.courseManagementService = courseManagementService;
        this.attendanceDownloadService = attendanceDownloadService;
    }

    @GetMapping("/dean/floated")
    public String students(Model model) {
        model.addAttribute("page_title", "Floated Courses");
        model.addAttribute("page_subtitle", "This session's floated courses");
        model.addAttribute("page_description", "Search and view this session's floated courses for all departments");
        return "dean/floated_page";
    }

    @GetMapping("dean/floated/{id}/attendance/download")
    public void downloadAttendance(HttpServletResponse response, @PathVariable String id) throws IOException {
        FloatedCourse floatedCourse = courseManagementService.getFloatedCourseByCode(id);

        if (floatedCourse == null)
            throw new AccessDeniedException("403");

        List<CourseRegistration> courseRegistrations = floatedCourse.getCourseRegistrations();

        response.setContentType("text/csv");
        response.setHeader("Content-disposition", "attachment;filename=attendance_" + id + ".csv");

        List<String> lines = attendanceDownloadService.attendanceCsv("dean", id, courseRegistrations);
        for (String line : lines) {
            response.getOutputStream().println(line);
        }

        response.getOutputStream().flush();
    }

}
