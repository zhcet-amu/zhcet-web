package in.ac.amu.zhcet.controller.department;

import in.ac.amu.zhcet.data.model.*;
import in.ac.amu.zhcet.service.CourseInChargeService;
import in.ac.amu.zhcet.service.CourseManagementService;
import in.ac.amu.zhcet.service.csv.RegistrationUploadService;
import in.ac.amu.zhcet.service.misc.AttendanceDownloadService;
import in.ac.amu.zhcet.utils.Utils;
import in.ac.amu.zhcet.utils.page.Path;
import in.ac.amu.zhcet.utils.page.PathChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
public class FloatedCourseController {

    private final CourseInChargeService courseInChargeService;
    private final CourseManagementService courseManagementService;
    private final AttendanceDownloadService attendanceDownloadService;
    private final RegistrationUploadService registrationUploadService;

    @Autowired
    public FloatedCourseController(CourseInChargeService courseInChargeService, CourseManagementService courseManagementService, AttendanceDownloadService attendanceDownloadService, RegistrationUploadService registrationUploadService) {
        this.courseInChargeService = courseInChargeService;
        this.courseManagementService = courseManagementService;
        this.attendanceDownloadService = attendanceDownloadService;
        this.registrationUploadService = registrationUploadService;
    }

    public static PathChain getPath(Department department, Course course) {
        return CoursesController.getPath(department)
                .add(Path.builder().title(course.getCode())
                        .link(String.format("/department/%s/courses/%s/edit", department.getCode(), course.getCode()))
                        .build())
                .add(Path.builder().title("Manage")
                        .link(String.format("/department/%s/floated/%s", department.getCode(), course.getCode()))
                        .build());
    }

    @PreAuthorize("isOfDepartment(#department, #course)")
    @GetMapping("department/{department}/floated/{course}")
    public String courseDetail(Model model, @PathVariable Department department, @PathVariable Course course, WebRequest webRequest) {
        FloatedCourse floatedCourse = courseManagementService.getFloatedCourseByCourse(course);

        if (!model.containsAttribute("success"))
            webRequest.removeAttribute("confirmRegistration", RequestAttributes.SCOPE_SESSION);

        model.addAttribute("page_title", course.getCode() + " - " + course.getTitle());
        model.addAttribute("page_subtitle", "Course management for " + course.getCode());
        model.addAttribute("page_description", "Register Students and add Faculty In-Charge for the course");
        model.addAttribute("page_path", getPath(department, course));

        List<CourseRegistration> courseRegistrations = floatedCourse.getCourseRegistrations();
        Utils.sortCourseAttendance(courseRegistrations);
        model.addAttribute("courseRegistrations", courseRegistrations);
        model.addAttribute("floatedCourse", floatedCourse);
        model.addAttribute("sections", courseInChargeService.getSections(floatedCourse));

        return "department/floated_course";
    }

    @PreAuthorize("isOfDepartment(#department, #course)")
    @GetMapping("department/{department}/floated/{course}/unfloat")
    public String unfloat(RedirectAttributes redirectAttributes, @PathVariable Department department, @PathVariable Course course) {
        courseManagementService.unfloatCourse(courseManagementService.getFloatedCourseByCourse(course));
        redirectAttributes.addFlashAttribute("course_success", "Course " + course.getCode() + " unfloated successfully!");

        return "redirect:/department/{department}/courses?active=true";
    }

    @PreAuthorize("isOfDepartment(#department, #course)")
    @PostMapping("department/{department}/floated/{course}/register")
    public String uploadFile(RedirectAttributes attributes, @PathVariable Department department, @PathVariable Course course, @RequestParam MultipartFile file, HttpSession session) {
        registrationUploadService.upload(course, file, attributes, session);

        return "redirect:/department/{department}/floated/{course}";
    }

    @PreAuthorize("isOfDepartment(#department, #course)")
    @PostMapping("department/{department}/floated/{course}/register/confirm")
    public String confirmRegistration(RedirectAttributes attributes, @PathVariable Department department, @PathVariable Course course, HttpSession session) {
        registrationUploadService.register(course, attributes, session);

        return "redirect:/department/{department}/floated/{course}";
    }

    private CourseInCharge create(String facultyId, String section) {
        CourseInCharge courseInCharge = new CourseInCharge();
        courseInCharge.setSection(section);
        courseInCharge.setFacultyMember(FacultyMember.builder().facultyId(facultyId).build());

        return courseInCharge;
    }

    private List<CourseInCharge> merge(List<String> facultyId, List<String> section) {
        if (section == null)
            section = Collections.nCopies(facultyId.size(), null);
        if (facultyId.size() < section.size())
            return null;

        List<CourseInCharge> courseInCharges = new ArrayList<>();
        for (int i = 0; i < section.size(); i++)
            courseInCharges.add(create(facultyId.get(i), section.get(i)));

        for (int i = section.size(); i < facultyId.size(); i++)
            courseInCharges.add(create(facultyId.get(i), null));

        return courseInCharges;
    }

    @PostMapping("department/{department}/floated/{course}/in_charge")
    public String addInCharge(RedirectAttributes redirectAttributes, @PathVariable Department department, @PathVariable Course course, @RequestParam(required = false) List<String> facultyId, @RequestParam(required = false) List<String> section) {
        if (facultyId == null) {
            log.warn("Removed all course in charges : Course-{} Sections-{}", course.getCode(), section);
            courseInChargeService.setInCharge(course, Collections.emptyList());
        } else {
            courseInChargeService.setInCharge(course, merge(facultyId, section));
        }

        redirectAttributes.addFlashAttribute("incharge_success", "Course In-Charge saved successfully");
        return "redirect:/department/{department}/floated/{course}";
    }

    @GetMapping("department/{department}/floated/{course}/attendance/download")
    public void downloadAttendance(@PathVariable Department department, @PathVariable Course course, HttpServletResponse response) throws IOException {
        FloatedCourse floatedCourse = courseManagementService.getFloatedCourseByCourse(course);
        attendanceDownloadService.download(course.getCode(), "department", floatedCourse.getCourseRegistrations(), response);
    }

}
