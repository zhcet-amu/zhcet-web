package amu.zhcet.core.admin.department.floated.incharge;

import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.course.floated.FloatedCourseNotFoundException;
import amu.zhcet.data.course.floated.FloatedCourseService;
import amu.zhcet.data.user.faculty.FacultyMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/department/floated/{course}")
public class InChargeManagementController {

    private final FloatedCourseService floatedCourseService;
    private final InChargeManagementService inChargeManagementService;

    @Autowired
    public InChargeManagementController(FloatedCourseService floatedCourseService, InChargeManagementService inChargeManagementService) {
        this.floatedCourseService = floatedCourseService;
        this.inChargeManagementService = inChargeManagementService;
    }

    @PostMapping("/in_charge")
    public String changeInCharge(RedirectAttributes redirectAttributes,
                                 @PathVariable Course course,
                                 @RequestParam(required = false) List<FacultyMember> facultyId,
                                 @RequestParam(required = false) List<String> section) {
        FloatedCourse floatedCourse = floatedCourseService.getFloatedCourse(course).orElseThrow(FloatedCourseNotFoundException::new);
        inChargeManagementService.saveInCharge(floatedCourse, facultyId, section);

        redirectAttributes.addFlashAttribute("incharge_success", "Course In-Charge saved successfully");

        return "redirect:/admin/department/floated/{course}";
    }

}
