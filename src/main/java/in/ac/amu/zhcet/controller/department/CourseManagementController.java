package in.ac.amu.zhcet.controller.department;

import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.RegistrationUpload;
import in.ac.amu.zhcet.service.core.CourseManagementService;
import in.ac.amu.zhcet.service.core.FacultyService;
import in.ac.amu.zhcet.service.core.upload.RegistrationUploadService;
import in.ac.amu.zhcet.service.core.upload.base.Confirmation;
import in.ac.amu.zhcet.service.core.upload.base.UploadResult;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
public class CourseManagementController {

    private final FacultyService facultyService;
    private final CourseManagementService courseManagementService;
    private final RegistrationUploadService registrationUploadService;

    @Autowired
    public CourseManagementController(FacultyService facultyService, CourseManagementService courseManagementService, RegistrationUploadService registrationUploadService) {
        this.facultyService = facultyService;
        this.courseManagementService = courseManagementService;
        this.registrationUploadService = registrationUploadService;
    }

    private FloatedCourse verifyAndGetCourse(String courseId) {
        FloatedCourse floatedCourse = courseManagementService.getCourseAndVerify(courseId);
        if (floatedCourse == null || !floatedCourse.getCourse().getDepartment().equals(facultyService.getFacultyDepartment()))
            throw new AccessDeniedException("403");

        return floatedCourse;
    }

    @GetMapping("department/courses/{id}")
    public String courseDetail(Model model, @PathVariable String id) {
        FloatedCourse floatedCourse = verifyAndGetCourse(id);

        model.addAttribute("page_title", floatedCourse.getCourse().getTitle());
        model.addAttribute("page_subtitle", "Course management for " + floatedCourse.getCourse().getCode());
        model.addAttribute("page_description", "Register Students and add Faculty In-Charge for the course");

        List<CourseRegistration> courseRegistrations = floatedCourse.getCourseRegistrations();
        Utils.sortAttendance(courseRegistrations);
        model.addAttribute("courseRegistrations", courseRegistrations);
        model.addAttribute("floatedCourse", floatedCourse);
        return "department/floated_course";
    }

    @PostMapping("department/courses/{id}/register")
    public String uploadFile(RedirectAttributes attributes, @PathVariable String id, @RequestParam("file") MultipartFile file) {
        verifyAndGetCourse(id);
        try {
            UploadResult<RegistrationUpload> result = registrationUploadService.handleUpload(file);

            if (!result.getErrors().isEmpty()) {
                attributes.addFlashAttribute("errors", result.getErrors());
            } else {
                attributes.addFlashAttribute("success", true);
                Confirmation<Student, String> confirmation = registrationUploadService.confirmUpload(id, result);
                attributes.addFlashAttribute("confirmRegistration", confirmation);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return "redirect:/department/courses/{id}";
    }

    @PostMapping("department/courses/{id}/confirm_registration")
    public String confirmRegistration(RedirectAttributes attributes, @PathVariable String id, @RequestParam List<String> studentId, @RequestParam List<String> mode) {
        verifyAndGetCourse(id);
        try {
            registrationUploadService.registerStudents(id, studentId, mode);
            attributes.addFlashAttribute("registered", true);
        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("unknown_error", true);
        }

        return "redirect:/department/courses/{id}";
    }

    @GetMapping("department/courses/{id}/add_in_charge")
    public String addInCharge(Model model, RedirectAttributes redirectAttributes, @PathVariable String id) {
        verifyAndGetCourse(id);
        redirectAttributes.addFlashAttribute("facultyMembers", facultyService.getByDepartment(facultyService.getFacultyDepartment()));
        return "redirect:/department/courses/{id}";
    }

    @PostMapping("department/courses/{id}/confirm_in_charge")
    public String confirmInCharge(Model model, RedirectAttributes redirectAttributes, @PathVariable String id, @RequestParam String facultyId) {
        verifyAndGetCourse(id);
        try {
            courseManagementService.addInCharge(id, Collections.singletonList(facultyId));
            redirectAttributes.addFlashAttribute("incharge_success", true);
            return "redirect:/department/courses/{id}";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("incharge_errors", Collections.singletonList(e.getLocalizedMessage()));
            return "redirect:/department/courses/{id}/add_in_charge";
        }
    }

}
