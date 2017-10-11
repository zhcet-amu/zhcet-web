package in.ac.amu.zhcet.controller.department;

import in.ac.amu.zhcet.data.CourseType;
import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.service.core.CourseManagementService;
import in.ac.amu.zhcet.service.core.FacultyService;
import in.ac.amu.zhcet.utils.UpdateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Slf4j
@Controller
public class CourseEditController {

    private final FacultyService facultyService;
    private final CourseManagementService courseManagementService;

    @Autowired
    public CourseEditController(FacultyService facultyService, CourseManagementService courseManagementService) {
        this.facultyService = facultyService;
        this.courseManagementService = courseManagementService;
    }

    private Course verifyAndGetCourse(String courseId) {
        Course course = courseManagementService.getCourseByCode(courseId);
        verifyCourse(course);

        return course;
    }

    private void verifyCourse(Course course) {
        if (course == null || !course.getDepartment().equals(facultyService.getFacultyDepartment()))
            throw new AccessDeniedException("403");
    }

    @GetMapping("/department/courses/{id}/edit")
    public String addCourse(Model model, @PathVariable String id) {
        model.addAttribute("page_description", "Edit course details and manage other settings");
        FacultyMember facultyMember = facultyService.getLoggedInMember();
        Department department = FacultyService.getDepartment(facultyMember);
        model.addAttribute("department", department);
        model.addAttribute("page_title", "Edit Course : " + department.getName() + " Department");
        model.addAttribute("page_subtitle", "Course Management");

        model.addAttribute("course_types", CourseType.values());

        if (!model.containsAttribute("course")) {
            Course course = verifyAndGetCourse(id);
            model.addAttribute("course", course);
        }

        FloatedCourse floatedCourse = courseManagementService.getFloatedCourseByCode(id);
        if (floatedCourse != null)
            model.addAttribute("floated", true);

        return "department/edit_course";
    }

    @PostMapping("/department/courses/{id}/edit")
    public String postCourse(@Valid Course course, BindingResult result, RedirectAttributes redirectAttributes, @PathVariable String id) {
        verifyCourse(course);
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("course", course);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.course", result);
        } else {
            try {
                course.setDepartment(facultyService.getFacultyDepartment());
                courseManagementService.saveCourse(id, course);
                redirectAttributes.addFlashAttribute("course_success", "Course saved successfully!");

                return "redirect:/department/courses/{id}/edit";
            } catch (UpdateException e) {
                log.warn("Course Save Error", e);
                course.setCode(id);
                redirectAttributes.addFlashAttribute("course", course);
                redirectAttributes.addFlashAttribute("course_errors", e.getMessage());
            }
        }

        return "redirect:/department/courses/{id}/edit";
    }

    @GetMapping("/department/courses/{id}/delete")
    public String deleteCourse(RedirectAttributes redirectAttributes, @PathVariable String id) {
        Course course = verifyAndGetCourse(id);

        if (course == null) {
            log.warn("Course not deletable %s", id);
            redirectAttributes.addFlashAttribute("course_error", "No such course exists");
        } else {
            courseManagementService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("course_success", "Course " + id + " deleted successfully!");
        }

        return "redirect:/department/courses?active=true";
    }
}
