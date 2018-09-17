package amu.zhcet.core.admin.attendance;

import amu.zhcet.common.utils.StringUtils;
import amu.zhcet.core.error.ErrorUtils;
import amu.zhcet.data.attendance.Attendance;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.course.floated.FloatedCourseNotFoundException;
import amu.zhcet.data.course.floated.FloatedCourseService;
import amu.zhcet.data.course.incharge.CourseInCharge;
import amu.zhcet.data.course.incharge.CourseInChargeNotFoundException;
import amu.zhcet.data.course.incharge.CourseInChargeService;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.data.department.Department;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Controller for downloading attendance from Dean, Department and Faculty Admin accounts
 * This controller and package presents shared functionality between dean, department and faculty
 * For separate functionality, please see the respective packages of `dean`, `department` and `faculty`
 */
@Slf4j
@Controller
public class AttendanceDownloadController {

    private final AttendanceDownloadService attendanceDownloadService;
    private final FloatedCourseService floatedCourseService;
    private final CourseInChargeService courseInChargeService;

    @Autowired
    public AttendanceDownloadController(AttendanceDownloadService attendanceDownloadService, FloatedCourseService floatedCourseService, CourseInChargeService courseInChargeService) {
        this.attendanceDownloadService = attendanceDownloadService;
        this.floatedCourseService = floatedCourseService;
        this.courseInChargeService = courseInChargeService;
    }

    /**
     * Downloads attendance for course taught by faculty. Shown in attendance upload section of the course in Faculty Panel
     * @param response Response object to be sent, containing the attendance CSV
     * @param code The course and section code for faculty, of the form course:section
     */
    @GetMapping("/admin/faculty/courses/{code}/attendance.csv")
    public ResponseEntity<InputStreamResource> downloadAttendanceForFaculty(HttpServletResponse response, @PathVariable String code) {
        CourseInCharge courseInCharge = courseInChargeService.getCourseInCharge(code).orElseThrow(CourseInChargeNotFoundException::new);
        String section = StringUtils.defaultString(CourseInChargeService.getCodeAndSection(code).getRight(), "all");
        List<CourseRegistration> courseRegistrations = courseInChargeService.getCourseRegistrations(courseInCharge);
        String suffix = courseInCharge.getFloatedCourse().getCourse().getCode() + "_" + section;
        return downloadAttendance("faculty", suffix, courseRegistrations);
    }

    /**
     * Downloads attendance for course in Dean Admin Panel in Floated Course Edit section
     * @param course Course for which the attendance is to be downloaded
     * @param response Response object to be sent, containing the attendance CSV
     */
    @GetMapping("/admin/dean/floated/{course}/attendance.csv")
    public ResponseEntity<InputStreamResource> downloadAttendanceForDean(@PathVariable Course course, HttpServletResponse response) {
        ErrorUtils.requireNonNullCourse(course);
        return downloadAttendance("dean", course);
    }

    /**
     * Downloads attendance for the course in Department Admin Panel in Floated Course Edit Section
     * @param department Department to which the course belongs
     * @param course Course for which the attendance is to be downloaded
     * @param response Response object to be sent, containing the attendance CSV
     */
    @GetMapping("/admin/department/floated/{course}/attendance.csv")
    public ResponseEntity<InputStreamResource> downloadAttendanceForDepartment(@PathVariable Course course, HttpServletResponse response) {
        ErrorUtils.requireNonNullCourse(course);
        return downloadAttendance("department", course);
    }

    private ResponseEntity<InputStreamResource> downloadAttendance(String context, Course course) {
        FloatedCourse floatedCourse = floatedCourseService.getFloatedCourse(course).orElseThrow(FloatedCourseNotFoundException::new);
        return downloadAttendance(context, course.getCode(), floatedCourse.getCourseRegistrations());
    }

    private ResponseEntity<InputStreamResource> downloadAttendance(String context, String suffix, List<CourseRegistration> courseRegistrations) {
        try {
           InputStream stream = attendanceDownloadService.getAttendanceStream(suffix, context, courseRegistrations);

            String lastModified = getLastModifiedDate(courseRegistrations)
                    .map(localDateTime -> "_" + localDateTime.format(DateTimeFormatter.ISO_DATE_TIME))
                    .orElse("");

           return ResponseEntity.ok()
                   .contentType(MediaType.parseMediaType("text/csv"))
                   .header("Content-disposition",
                           "attachment;filename=attendance_" + suffix + lastModified + ".csv")
                   .body(new InputStreamResource(stream));
        } catch (IOException e) {
            log.error("Attendance Download Error", e);

            return ResponseEntity.notFound().build();
        }
    }

    private Optional<LocalDateTime> getLastModifiedDate(List<CourseRegistration> courseRegistrations) {
        if (courseRegistrations == null)
            return Optional.empty();

        return courseRegistrations.stream()
                .map(CourseRegistration::getAttendance)
                .max(Comparator.comparing(Attendance::getModifiedAt))
                .map(Attendance::getModifiedAt);
    }

}
