package in.ac.amu.zhcet.controller.department;

import in.ac.amu.zhcet.data.model.*;
import in.ac.amu.zhcet.data.model.dto.upload.RegistrationUpload;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
public class FloatedCourseController {

    private final FacultyService facultyService;
    private final CourseManagementService courseManagementService;
    private final RegistrationUploadService registrationUploadService;

    @Autowired
    public FloatedCourseController(FacultyService facultyService, CourseManagementService courseManagementService, RegistrationUploadService registrationUploadService) {
        this.facultyService = facultyService;
        this.courseManagementService = courseManagementService;
        this.registrationUploadService = registrationUploadService;
    }

    private FloatedCourse verifyAndGetCourse(String courseId) {
        FloatedCourse floatedCourse = courseManagementService.getFloatedCourseByCode(courseId);
        if (floatedCourse == null || !floatedCourse.getCourse().getDepartment().equals(facultyService.getFacultyDepartment()))
            throw new AccessDeniedException("403");

        return floatedCourse;
    }

    @GetMapping("department/floated/{id}")
    public String courseDetail(Model model, @PathVariable String id) {
        FloatedCourse floatedCourse = verifyAndGetCourse(id);

        model.addAttribute("page_title", floatedCourse.getCourse().getTitle());
        model.addAttribute("page_subtitle", "Course management for " + floatedCourse.getCourse().getCode());
        model.addAttribute("page_description", "Register Students and add Faculty In-Charge for the course");

        List<CourseRegistration> courseRegistrations = floatedCourse.getCourseRegistrations();
        Utils.sortAttendance(courseRegistrations);
        model.addAttribute("courseRegistrations", courseRegistrations);
        model.addAttribute("floatedCourse", floatedCourse);
        model.addAttribute("sections", courseManagementService.getSections(floatedCourse));

        return "department/floated_course";
    }

    @GetMapping("department/floated/{id}/unfloat")
    public String unfloat(RedirectAttributes redirectAttributes, @PathVariable String id) {
        courseManagementService.unfloatCourse(verifyAndGetCourse(id));
        redirectAttributes.addFlashAttribute("course_success", "Course " + id + " unfloated successfully!");

        return "redirect:/department/courses?active=true";
    }

    private List<CourseInCharge> merge(List<String> facultyId, List<String> section) {
        if (facultyId.size() < section.size())
            return null;

        List<CourseInCharge> courseInCharges = new ArrayList<>();
        for (int i = 0; i < section.size(); i++) {
            CourseInCharge courseInCharge = new CourseInCharge();
            courseInCharge.setSection(section.get(i));
            courseInCharge.setFacultyMember(FacultyMember.builder().facultyId(facultyId.get(i)).build());
            courseInCharges.add(courseInCharge);
        }

        for (int i = section.size(); i < facultyId.size(); i++) {
            CourseInCharge courseInCharge = new CourseInCharge();
            courseInCharge.setSection(null);
            courseInCharge.setFacultyMember(FacultyMember.builder().facultyId(facultyId.get(i)).build());
            courseInCharges.add(courseInCharge);
        }

        return courseInCharges;
    }

    @PostMapping("department/floated/{id}/in_charge")
    public String addInCharge(RedirectAttributes redirectAttributes, @PathVariable String id, @RequestParam(required = false) List<String> facultyId, @RequestParam(required = false) List<String> section) {
        verifyAndGetCourse(id);

        if (facultyId == null) {
            courseManagementService.setInCharge(id, Collections.emptyList());
        } else if (section == null) {
            courseManagementService.setInCharge(id, merge(facultyId, Collections.nCopies(facultyId.size(), null)));
        } else {
            courseManagementService.setInCharge(id, merge(facultyId, section));
        }

        redirectAttributes.addFlashAttribute("incharge_success", "Course In-Charge saved successfully");

        return "redirect:/department/floated/{id}";
    }

    @PostMapping("department/floated/{id}/register")
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

        return "redirect:/department/floated/{id}";
    }

    @PostMapping("department/floated/{id}/confirm_registration")
    public String confirmRegistration(RedirectAttributes attributes, @PathVariable String id, @RequestParam List<String> studentId, @RequestParam List<String> mode) {
        verifyAndGetCourse(id);
        try {
            registrationUploadService.registerStudents(id, studentId, mode);
            attributes.addFlashAttribute("registered", true);
        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("unknown_error", true);
        }

        return "redirect:/department/floated/{id}";
    }

}
