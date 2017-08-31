package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.service.DepartmentAdminService;
import in.ac.amu.zhcet.data.service.FacultyService;
import in.ac.amu.zhcet.data.service.upload.RegistrationUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
public class DepartmentController {

    private final DepartmentAdminService departmentAdminService;
    private final RegistrationUploadService registrationUploadService;

    public DepartmentController(DepartmentAdminService departmentAdminService, RegistrationUploadService registrationUploadService) {
        this.departmentAdminService = departmentAdminService;
        this.registrationUploadService = registrationUploadService;
    }

    @GetMapping("/department")
    public String department(Model model) {
        model.addAttribute("floatedCourses", departmentAdminService.getFloatedCourses());
        model.addAttribute("courses", departmentAdminService.getAllCourses());
        FacultyMember facultyMember = departmentAdminService.getFacultyMember();
        model.addAttribute("department", FacultyService.getDepartment(facultyMember));
        if (!model.containsAttribute("course")) {
            Course course = new Course();
            course.setDepartment(FacultyService.getDepartment(facultyMember));
            model.addAttribute("course", course);
        }
        model.addAttribute("facultyMembers", departmentAdminService.getAllFacultyMembers());

        return "department";
    }

    @PostMapping("/department/create_course")
    public String createCourse(@Valid Course course, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors() && !bindingResult.hasFieldErrors("department")) {
            redirectAttributes.addFlashAttribute("course", course);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.course", bindingResult);
        } else {
            try {
                departmentAdminService.registerCourse(course);
                redirectAttributes.addFlashAttribute("course_success", true);
            } catch (Exception e) {
                List<String> errors = new ArrayList<>();
                if (e.getMessage().contains("PRIMARY")) {
                    errors.add("Course with this code already exists");
                }

                redirectAttributes.addFlashAttribute("course", course);
                redirectAttributes.addFlashAttribute("course_errors", errors);
            }
        }

        return "redirect:/department";
    }

    @PostMapping("/department/float_course")
    public String floatCourse(@RequestParam String courseCode, @RequestParam List<String> faculty, RedirectAttributes redirectAttributes) {
        Course course = departmentAdminService.findCourseByCode(courseCode);

        List<String> errors = new ArrayList<>();
        if (course == null) {
            errors.add("No valid course selected");
        } else {
            try {
                departmentAdminService.floatCourse(course, faculty);
                redirectAttributes.addFlashAttribute("float_success", true);
            } catch (Exception exc) {
                if (exc.getMessage().contains("PRIMARY")) {
                    errors.add("This course is already floated!");
                }
            }
        }

        redirectAttributes.addFlashAttribute("float_errors", errors);

        return "redirect:/department";
    }

    private FloatedCourse verifyAndGetCourse(String courseId) {
        FloatedCourse floatedCourse = departmentAdminService.getCourseById(courseId);
        if (!floatedCourse.getCourse().getDepartment().equals(departmentAdminService.getFacultyMember().getUser().getDetails().getDepartment()))
            throw new IllegalArgumentException("Unauthorized access");

        return floatedCourse;
    }

    @GetMapping("department/courses/{id}")
    public String courseDetail(Model model, @PathVariable String id) {
        FloatedCourse floatedCourse = verifyAndGetCourse(id);

        List<CourseRegistration> courseRegistrations = floatedCourse.getCourseRegistrations();
        model.addAttribute("courseRegistrations", courseRegistrations);
        model.addAttribute("floatedCourse", floatedCourse);
        return "floated_course";
    }

    @PostMapping("department/courses/{id}/register")
    public String uploadFile(RedirectAttributes attributes, @PathVariable String id, @RequestParam("file") MultipartFile file) {
        try {
            RegistrationUploadService.UploadResult result = registrationUploadService.handleUpload(file);

            if (!result.getErrors().isEmpty()) {
                attributes.addFlashAttribute("errors", result.getErrors());
            } else {
                attributes.addFlashAttribute("success", true);
                RegistrationUploadService.RegistrationConfirmation confirmation = registrationUploadService.confirmUpload(id, result);

                log.info(confirmation.toString());
                attributes.addFlashAttribute("confirmRegistration", confirmation);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return "redirect:/department/courses/{id}";
    }

    @PostMapping("department/courses/{id}/confirm_registration")
    public String confirmRegistration(RedirectAttributes attributes, @PathVariable String id, @RequestParam List<String> studentId) {

        try {
            registrationUploadService.registerStudents(id, studentId);
            attributes.addFlashAttribute("registered", true);
        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("unknown_error", true);
        }

        return "redirect:/department/courses/{id}";
    }

    @GetMapping("department/courses/{id}/add_in_charge")
    public String addInCharge(Model model, RedirectAttributes redirectAttributes, @PathVariable String id) {
        redirectAttributes.addFlashAttribute("facultyMembers", departmentAdminService.getAllFacultyMembers());
        return "redirect:/department/courses/{id}";
    }

    @PostMapping("department/courses/{id}/confirm_in_charge")
    public String confirmInCharge(Model model, RedirectAttributes redirectAttributes, @PathVariable String id, @RequestParam String facultyId) {
        verifyAndGetCourse(id);
        try {
            departmentAdminService.addInCharge(id, facultyId);
            redirectAttributes.addFlashAttribute("incharge_success", true);
            return "redirect:/department/courses/{id}";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("incharge_errors", Collections.singletonList(e.getLocalizedMessage()));
            return "redirect:/department/courses/{id}/add_in_charge";
        }
    }

}
