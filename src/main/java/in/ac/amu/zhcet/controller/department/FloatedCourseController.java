package in.ac.amu.zhcet.controller.department;

import in.ac.amu.zhcet.data.model.CourseInCharge;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.service.misc.AttendanceDownloadService;
import in.ac.amu.zhcet.service.CourseInChargeService;
import in.ac.amu.zhcet.service.CourseManagementService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    public FloatedCourseController(CourseInChargeService courseInChargeService, CourseManagementService courseManagementService, AttendanceDownloadService attendanceDownloadService) {
        this.courseInChargeService = courseInChargeService;
        this.courseManagementService = courseManagementService;
        this.attendanceDownloadService = attendanceDownloadService;
    }

    @GetMapping("department/floated/{id}")
    public String courseDetail(Model model, @PathVariable String id, WebRequest webRequest) {
        FloatedCourse floatedCourse = courseManagementService.verifyAndGetCourse(id);

        if (!model.containsAttribute("success"))
            webRequest.removeAttribute("confirmRegistration", RequestAttributes.SCOPE_SESSION);

        model.addAttribute("page_title", floatedCourse.getCourse().getTitle());
        model.addAttribute("page_subtitle", "Course management for " + floatedCourse.getCourse().getCode());
        model.addAttribute("page_description", "Register Students and add Faculty In-Charge for the course");

        List<CourseRegistration> courseRegistrations = floatedCourse.getCourseRegistrations();
        Utils.sortCourseAttendance(courseRegistrations);
        model.addAttribute("courseRegistrations", courseRegistrations);
        model.addAttribute("floatedCourse", floatedCourse);
        model.addAttribute("sections", courseInChargeService.getSections(floatedCourse));

        return "department/floated_course";
    }

    @GetMapping("department/floated/{id}/unfloat")
    public String unfloat(RedirectAttributes redirectAttributes, @PathVariable String id) {
        courseManagementService.unfloatCourse(courseManagementService.verifyAndGetCourse(id));
        redirectAttributes.addFlashAttribute("course_success", "Course " + id + " unfloated successfully!");

        return "redirect:/department/courses?active=true";
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

    @PostMapping("department/floated/{id}/in_charge")
    public String addInCharge(RedirectAttributes redirectAttributes, @PathVariable String id, @RequestParam(required = false) List<String> facultyId, @RequestParam(required = false) List<String> section) {
        courseManagementService.verifyAndGetCourse(id);

        if (facultyId == null) {
            log.warn("Removed all course in charges : Course-{} Sections-{}", id, section);
            courseInChargeService.setInCharge(id, Collections.emptyList());
        } else {
            courseInChargeService.setInCharge(id, merge(facultyId, section));
        }

        redirectAttributes.addFlashAttribute("incharge_success", "Course In-Charge saved successfully");
        return "redirect:/department/floated/{id}";
    }

    @GetMapping("department/floated/{id}/attendance/download")
    public void getStudents(HttpServletResponse response, @PathVariable String id) throws IOException {
        FloatedCourse floatedCourse = courseManagementService.verifyAndGetCourse(id);

        List<CourseRegistration> courseRegistrations = floatedCourse.getCourseRegistrations();
        Utils.sortCourseAttendance(courseRegistrations);

        response.setContentType("text/csv");
        response.setHeader("Content-disposition", "attachment;filename=attendance_" + id + ".csv");

        List<String> lines = attendanceDownloadService.attendanceCsv("department", id, courseRegistrations);
        for (String line : lines) {
            response.getOutputStream().println(line);
        }

        response.getOutputStream().flush();
    }

}
