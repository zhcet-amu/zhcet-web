package in.ac.amu.zhcet.controller.department;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.service.core.CourseManagementService;
import in.ac.amu.zhcet.service.core.FacultyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class DepartmentController {

    private final FacultyService facultyService;
    private final CourseManagementService courseManagementService;

    @Autowired
    public DepartmentController(FacultyService facultyService, CourseManagementService departmentAdminService) {
        this.facultyService = facultyService;
        this.courseManagementService = departmentAdminService;
    }

    @GetMapping("/department")
    public String department(Model model) {
        FacultyMember facultyMember = facultyService.getLoggedInMember();
        Department department = FacultyService.getDepartment(facultyMember);
        model.addAttribute("page_description", "Manage and float courses for the session");
        model.addAttribute("floatedCourses", courseManagementService.getCurrentFloatedCourses(department));
        model.addAttribute("courses", courseManagementService.getAllCourses(department));
        model.addAttribute("department", department);
        model.addAttribute("page_title", department.getName() + " Department Panel");
        model.addAttribute("page_subtitle", "Course management for Department");
        if (!model.containsAttribute("course")) {
            Course course = new Course();
            course.setDepartment(FacultyService.getDepartment(facultyMember));
            model.addAttribute("course", course);
        }
        model.addAttribute("facultyMembers", facultyService.getByDepartment(department));

        return "department/admin";
    }

}
