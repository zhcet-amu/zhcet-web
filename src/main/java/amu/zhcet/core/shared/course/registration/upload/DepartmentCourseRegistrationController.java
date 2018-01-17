package amu.zhcet.core.shared.course.registration.upload;

import amu.zhcet.core.error.ErrorUtils;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.department.Department;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("/admin/department/{department}/floated/{course}/register")
public class DepartmentCourseRegistrationController {

    private final CourseRegistrationUploadService courseRegistrationUploadService;

    @Autowired
    public DepartmentCourseRegistrationController(CourseRegistrationUploadService courseRegistrationUploadService) {
        this.courseRegistrationUploadService = courseRegistrationUploadService;
    }

    /**
     * Handles the uploaded CSV in Department Panel.
     * Feature is presented in Floated Course Manage Page of Department Panel
     * @param attributes RedirectAttributes to be set
     * @param department Department to which the course belongs
     * @param course Course to which the students need to be registered
     * @param file MultipartFile containing CSV listing students to be registered
     * @param session HttpSession for storing intermediate information
     * @return Layout to be rendered
     */
    @PostMapping
    public String uploadFile(RedirectAttributes attributes, @PathVariable Department department, @PathVariable Course course, @RequestParam MultipartFile file, HttpSession session) {
        ErrorUtils.requireNonNullDepartment(department);
        ErrorUtils.requireNonNullCourse(course);
        courseRegistrationUploadService.upload(course, file, attributes, session);

        return "redirect:/admin/department/{department}/floated/{course}";
    }

    /**
     * Confirms the student registration information stored in HttpSession after asking the admin.
     * Feature is present in Floated Course Manage Page of Department Panel
     * @param attributes RedirectAttributes to be set
     * @param department Department to which the course belongs
     * @param course Course to which the students need to be registered
     * @param session HttpSession containing the registration information.
     *                To be cleared after successful registration
     * @return Layout to be rendered
     */
    @PostMapping("/confirm")
    public String confirmRegistration(RedirectAttributes attributes, @PathVariable Department department, @PathVariable Course course, HttpSession session) {
        ErrorUtils.requireNonNullDepartment(department);
        ErrorUtils.requireNonNullCourse(course);
        courseRegistrationUploadService.register(course, attributes, session);

        return "redirect:/admin/department/{department}/floated/{course}";
    }

}
