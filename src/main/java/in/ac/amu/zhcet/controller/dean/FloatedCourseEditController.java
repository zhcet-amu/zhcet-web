package in.ac.amu.zhcet.controller.dean;

import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.service.CourseManagementService;
import in.ac.amu.zhcet.service.csv.RegistrationUploadService;
import in.ac.amu.zhcet.service.misc.AttendanceDownloadService;
import in.ac.amu.zhcet.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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

@Slf4j
@Controller
public class FloatedCourseEditController {

    private final CourseManagementService courseManagementService;
    private final AttendanceDownloadService attendanceDownloadService;
    private final RegistrationUploadService registrationUploadService;

    @Autowired
    public FloatedCourseEditController(CourseManagementService courseManagementService, AttendanceDownloadService attendanceDownloadService, RegistrationUploadService registrationUploadService) {
        this.courseManagementService = courseManagementService;
        this.attendanceDownloadService = attendanceDownloadService;
        this.registrationUploadService = registrationUploadService;
    }

    @GetMapping("/dean/floated")
    public String students(Model model) {
        model.addAttribute("page_title", "Floated Courses");
        model.addAttribute("page_subtitle", "This session's floated courses");
        model.addAttribute("page_description", "Search and view this session's floated courses for all departments");
        return "dean/floated_page";
    }

    private FloatedCourse verifyAndGet(String id) {
        FloatedCourse floatedCourse = courseManagementService.getFloatedCourseByCode(id);
        if (floatedCourse == null)
            throw new AccessDeniedException("403");
        return floatedCourse;
    }

    @GetMapping("dean/floated/{id}")
    public String courseDetail(Model model, @PathVariable String id, WebRequest webRequest) {
        FloatedCourse floatedCourse = verifyAndGet(id);

        if (!model.containsAttribute("success"))
            webRequest.removeAttribute("confirmRegistration", RequestAttributes.SCOPE_SESSION);

        model.addAttribute("page_title", floatedCourse.getCourse().getTitle());
        model.addAttribute("page_subtitle", "Course management for " + floatedCourse.getCourse().getCode());
        model.addAttribute("page_description", "Register Students for the Floated course");

        List<CourseRegistration> courseRegistrations = floatedCourse.getCourseRegistrations();
        Utils.sortCourseAttendance(courseRegistrations);
        model.addAttribute("courseRegistrations", courseRegistrations);
        model.addAttribute("floatedCourse", floatedCourse);
        model.addAttribute("deanOverride", "dean");

        return "dean/floated_course";
    }

    @PostMapping("dean/floated/{id}/register")
    public String uploadFile(RedirectAttributes attributes, @PathVariable String id, @RequestParam MultipartFile file, HttpSession session) {
        verifyAndGet(id);
        registrationUploadService.upload(id, file, attributes, session);

        return "redirect:/dean/floated/{id}";
    }

    @PostMapping("dean/floated/{id}/register/confirm")
    public String confirmRegistration(RedirectAttributes attributes, @PathVariable String id, HttpSession session) {
        verifyAndGet(id);
        registrationUploadService.register(id, attributes, session);

        return "redirect:/dean/floated/{id}";
    }

    @GetMapping("dean/floated/{id}/attendance/download")
    public void downloadAttendance(HttpServletResponse response, @PathVariable String id) throws IOException {
        FloatedCourse floatedCourse = verifyAndGet(id);
        attendanceDownloadService.download(id, "dean", floatedCourse.getCourseRegistrations(), response);
    }

}
