package amu.zhcet.core.department.floated.incharge;

import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.CourseManagementService;
import amu.zhcet.data.department.Department;
import amu.zhcet.data.user.faculty.FacultyMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
public class InChargeManagementController {

    private final CourseManagementService courseManagementService;
    private final InChargeManagementService inChargeManagementService;

    @Autowired
    public InChargeManagementController(CourseManagementService courseManagementService, InChargeManagementService inChargeManagementService) {
        this.courseManagementService = courseManagementService;
        this.inChargeManagementService = inChargeManagementService;
    }

    @PostMapping("department/{department}/floated/{course}/in_charge")
    public String changeInCharge(RedirectAttributes redirectAttributes,
                                 @PathVariable Department department,
                                 @PathVariable Course course,
                                 @RequestParam(required = false) List<FacultyMember> facultyId,
                                 @RequestParam(required = false) List<String> section) {
        courseManagementService.getFloatedCourse(course).ifPresent(floatedCourse -> {
            inChargeManagementService.saveInCharge(floatedCourse, facultyId, section);

            redirectAttributes.addFlashAttribute("incharge_success", "Course In-Charge saved successfully");
        });

        return "redirect:/department/{department}/floated/{course}";
    }

}
