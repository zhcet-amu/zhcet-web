package in.ac.amu.zhcet.controller.department;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.service.core.DepartmentAdminService;
import in.ac.amu.zhcet.service.core.FacultyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class DepartmentController {

    private final DepartmentAdminService departmentAdminService;

    public DepartmentController(DepartmentAdminService departmentAdminService) {
        this.departmentAdminService = departmentAdminService;
    }

    @GetMapping("/department")
    public String department(Model model) {
        model.addAttribute("title", "Department Panel");
        model.addAttribute("subtitle", "Course management for Department");
        model.addAttribute("description", "Manage and float courses for the session");
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

}
