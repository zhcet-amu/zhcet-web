package in.ac.amu.zhcet.controller.department;

import in.ac.amu.zhcet.data.CourseType;
import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.service.core.CourseManagementService;
import in.ac.amu.zhcet.service.core.FacultyService;
import in.ac.amu.zhcet.utils.DuplicateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Slf4j
@Controller
public class CourseCreationController {

    private final FacultyService facultyService;
    private final CourseManagementService courseManagementService;

    @Autowired
    public CourseCreationController(FacultyService facultyService, CourseManagementService courseManagementService) {
        this.facultyService = facultyService;
        this.courseManagementService = courseManagementService;
    }

    @GetMapping("/department/course/add")
    public String addCourse(Model model) {
        model.addAttribute("page_description", "Create new global course for the Department");
        FacultyMember facultyMember = facultyService.getLoggedInMember();
        Department department = FacultyService.getDepartment(facultyMember);
        model.addAttribute("department", department);
        model.addAttribute("page_title", "Add Course : " + department.getName() + " Department");
        model.addAttribute("page_subtitle", "Course Management");

        model.addAttribute("course_types", CourseType.values());

        if (!model.containsAttribute("course")) {
            Course course = new Course();
            course.setDepartment(FacultyService.getDepartment(facultyMember));
            model.addAttribute("course", course);
        }

        return "department/add_course";
    }

    @PostMapping("/department/course/add")
    public String postCourse(@Valid Course course, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("course", course);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.course", result);
        } else {
            try {
                course.setDepartment(facultyService.getFacultyDepartment());
                courseManagementService.addCourse(course);
                redirectAttributes.addFlashAttribute("course_success", "Course created successfully!");

                return "redirect:/department/courses?active=true";
            } catch (DuplicateException e) {
                redirectAttributes.addFlashAttribute("course", course);
                redirectAttributes.addFlashAttribute("course_errors", e.getMessage());
            }
        }

        return "redirect:/department/course/add";
    }

}
