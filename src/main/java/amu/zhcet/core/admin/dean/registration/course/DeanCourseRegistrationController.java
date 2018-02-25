package amu.zhcet.core.admin.dean.registration.course;

import amu.zhcet.core.error.ErrorUtils;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.data.course.registration.upload.CourseRegistrationUploadService;
import amu.zhcet.storage.csv.Confirmation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("/admin/dean/floated/{course}/register")
public class DeanCourseRegistrationController {

    private final CourseRegistrationUploadService courseRegistrationUploadService;

    @Autowired
    public DeanCourseRegistrationController(CourseRegistrationUploadService courseRegistrationUploadService) {
        this.courseRegistrationUploadService = courseRegistrationUploadService;
    }

    /**
     * Handles the uploaded CSV in Department Panel.
     * Feature is presented in Floated Course Manage Page of Dean Admin Panel
     * @param attributes RedirectAttributes to be set
     * @param course Course to which the students need to be registered
     * @param file MultipartFile containing CSV listing students to be registered
     * @param session HttpSession for storing intermediate information
     * @return Layout to be rendered
     */
    @PostMapping
    public String uploadFile(RedirectAttributes attributes, @PathVariable Course course, @RequestParam MultipartFile file, HttpSession session) {
        ErrorUtils.requireNonNullCourse(course);
        courseRegistrationUploadService.upload(course, file, attributes, session);

        return "redirect:/admin/dean/floated/{course}";
    }

    /**
     * Confirms the student registration information stored in HttpSession after asking the admin.
     * Feature is present in Floated Course Manage Page of Dean Admin Panel
     * @param attributes RedirectAttributes to be set
     * @param course Course to which the students need to be registered
     * @param registrations Course registration confirmation
     * @return Layout to be rendered
     */
    @PostMapping("/confirm")
    public String confirmRegistration(RedirectAttributes attributes, @PathVariable Course course,
                                      @SessionAttribute("confirmRegistration") Confirmation<CourseRegistration> registrations) {
        ErrorUtils.requireNonNullCourse(course);
        courseRegistrationUploadService.register(course, attributes, registrations);

        return "redirect:/admin/dean/floated/{course}";
    }

}
