package amu.zhcet.core.department.floated;

import amu.zhcet.common.flash.Flash;
import amu.zhcet.common.page.Path;
import amu.zhcet.common.page.PathChain;
import amu.zhcet.common.utils.SortUtils;
import amu.zhcet.core.department.course.CoursesController;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.CourseManagementService;
import amu.zhcet.data.course.incharge.CourseInCharge;
import amu.zhcet.data.course.incharge.CourseInChargeService;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.data.course.registration.CourseRegistrationService;
import amu.zhcet.data.department.Department;
import amu.zhcet.data.user.faculty.FacultyMember;
import amu.zhcet.data.user.student.Student;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class FloatedCourseController {

    private final CourseInChargeService courseInChargeService;
    private final CourseRegistrationService courseRegistrationService;
    private final CourseManagementService courseManagementService;

    @Autowired
    public FloatedCourseController(
            CourseInChargeService courseInChargeService,
            CourseRegistrationService courseRegistrationService,
            CourseManagementService courseManagementService
    ) {
        this.courseInChargeService = courseInChargeService;
        this.courseRegistrationService = courseRegistrationService;
        this.courseManagementService = courseManagementService;
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

    @GetMapping("department/{department}/floated/{course}")
    public String courseDetail(Model model, @PathVariable Department department, @PathVariable Course course, WebRequest webRequest) {
        String templateUrl = "department/floated_course";
        if (department == null)
            return templateUrl;

        courseManagementService.getFloatedCourse(course).ifPresent(floatedCourse -> {
            if (!model.containsAttribute("success"))
                webRequest.removeAttribute("confirmRegistration", RequestAttributes.SCOPE_SESSION);

            model.addAttribute("page_title", course.getCode() + " - " + course.getTitle());
            model.addAttribute("page_subtitle", "Course management for " + course.getCode());
            model.addAttribute("page_description", "Register Students and add Faculty In-Charge for the course");
            model.addAttribute("page_path", getPath(department, course));

            List<CourseRegistration> courseRegistrations = floatedCourse.getCourseRegistrations();
            List<String> emails = CourseManagementService
                    .getEmailsFromCourseRegistrations(courseRegistrations.stream())
                    .collect(Collectors.toList());
            SortUtils.sortCourseAttendance(courseRegistrations);
            model.addAttribute("courseRegistrations", courseRegistrations);
            model.addAttribute("floatedCourse", floatedCourse);
            model.addAttribute("sections", courseInChargeService.getSections(floatedCourse));
            model.addAttribute("email_list", emails);
        });

        return templateUrl;
    }

    @PostMapping("department/{department}/floated/{course}/remove/{student}")
    public String removeStudent(RedirectAttributes attributes, @PathVariable Department department, @PathVariable Course course, @PathVariable Student student) {
        if (course != null && student != null) {
            courseRegistrationService.removeRegistration(course, student);
            attributes.addFlashAttribute("flash_messages", Flash.success("Student removed from course"));
        }

        return "redirect:/department/{department}/floated/{course}";
    }

    @GetMapping("department/{department}/floated/{course}/unfloat")
    public String unfloat(RedirectAttributes redirectAttributes, @PathVariable Department department, @PathVariable Course course) {
        String redirectUrl = "redirect:/department/{department}/courses?active=true";

        courseManagementService.getFloatedCourse(course).ifPresent(floatedCourse -> {
            courseManagementService.unfloatCourse(floatedCourse);
            redirectAttributes.addFlashAttribute("course_success", "Course " + course.getCode() + " unfloated successfully!");
        });

        return redirectUrl;
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
        String redirectUrl = "redirect:/department/{department}/floated/{course}";
        if (course == null)
            return redirectUrl;

        if (facultyId == null) {
            log.warn("Removed all course in charges : Course-{} Sections-{}", course.getCode(), section);
            courseInChargeService.setInCharge(course, Collections.emptyList());
        } else {
            courseInChargeService.setInCharge(course, merge(facultyId, section));
        }

        redirectAttributes.addFlashAttribute("incharge_success", "Course In-Charge saved successfully");
        return redirectUrl;
    }

}
