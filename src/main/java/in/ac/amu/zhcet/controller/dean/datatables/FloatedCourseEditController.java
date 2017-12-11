package in.ac.amu.zhcet.controller.dean.datatables;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.service.CourseManagementService;
import in.ac.amu.zhcet.service.CourseRegistrationService;
import in.ac.amu.zhcet.service.extra.AttendanceDownloadService;
import in.ac.amu.zhcet.service.upload.csv.RegistrationUploadService;
import in.ac.amu.zhcet.utils.Flash;
import in.ac.amu.zhcet.utils.SortUtils;
import in.ac.amu.zhcet.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class FloatedCourseEditController {

    private final CourseManagementService courseManagementService;
    private final CourseRegistrationService courseRegistrationService;
    private final AttendanceDownloadService attendanceDownloadService;
    private final RegistrationUploadService registrationUploadService;

    @Autowired
    public FloatedCourseEditController(CourseManagementService courseManagementService, CourseRegistrationService courseRegistrationService, AttendanceDownloadService attendanceDownloadService, RegistrationUploadService registrationUploadService) {
        this.courseManagementService = courseManagementService;
        this.courseRegistrationService = courseRegistrationService;
        this.attendanceDownloadService = attendanceDownloadService;
        this.registrationUploadService = registrationUploadService;
    }

    @GetMapping("/dean/floated")
    public String students(Model model) {
        model.addAttribute("page_title", "Floated Courses - " + Utils.getDefaultSessionName());
        model.addAttribute("page_subtitle", "This session's floated courses");
        model.addAttribute("page_description", "Search and view this session's floated courses for all departments");
        return "dean/floated_page";
    }

    @GetMapping("dean/floated/{course}")
    public String courseDetail(Model model, @PathVariable Course course, WebRequest webRequest) {
        String templateUrl = "dean/floated_course";
        courseManagementService.getFloatedCourse(course).ifPresent(floatedCourse -> {
            if (!model.containsAttribute("success"))
                webRequest.removeAttribute("confirmRegistration", RequestAttributes.SCOPE_SESSION);

            model.addAttribute("page_title", course.getCode() + " - " + course.getTitle());
            model.addAttribute("page_subtitle", "Course management for " + course.getCode());
            model.addAttribute("page_description", "Register Students for the Floated course");

            List<CourseRegistration> courseRegistrations = floatedCourse.getCourseRegistrations();
            List<String> emails = CourseManagementService
                    .getEmailsFromCourseRegistrations(courseRegistrations.stream())
                    .collect(Collectors.toList());
            SortUtils.sortCourseAttendance(courseRegistrations);
            model.addAttribute("courseRegistrations", courseRegistrations);
            model.addAttribute("floatedCourse", floatedCourse);
            model.addAttribute("deanOverride", "dean");
            model.addAttribute("email_list", emails);
        });

        return templateUrl;
    }

    @PostMapping("dean/floated/{course}/remove/{student}")
    public String removeStudent(RedirectAttributes attributes, @PathVariable Course course, @PathVariable Student student) {
        if (course != null && student != null) {
            courseRegistrationService.removeRegistration(course, student);
            attributes.addFlashAttribute("flash_messages", Flash.success("Student removed from course"));
        }

        return "redirect:/dean/floated/{course}";
    }

    @PostMapping("dean/floated/{course}/register")
    public String uploadFile(RedirectAttributes attributes, @PathVariable Course course, @RequestParam MultipartFile file, HttpSession session) {
        String redirectUrl = "redirect:/dean/floated/{course}";
        if (course == null)
            return redirectUrl;
        registrationUploadService.upload(course, file, attributes, session);

        return redirectUrl;
    }

    @PostMapping("dean/floated/{course}/register/confirm")
    public String confirmRegistration(RedirectAttributes attributes, @PathVariable Course course, HttpSession session) {
        String redirectUrl = "redirect:/dean/floated/{course}";
        if (course == null)
            return redirectUrl;
        registrationUploadService.register(course, attributes, session);

        return redirectUrl;
    }

    @GetMapping("dean/floated/{course}/attendance/download")
    public void downloadAttendance(@PathVariable Course course, HttpServletResponse response) throws IOException {
        courseManagementService.getFloatedCourse(course).ifPresent(floatedCourse -> {
            try {
                attendanceDownloadService.download(course.getCode(), "dean", floatedCourse.getCourseRegistrations(), response);
            } catch (IOException e) {
                log.error("Attendance Download", e);
            }
        });
    }

}
