package in.ac.amu.zhcet.controller.department;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.service.core.CourseManagementService;
import in.ac.amu.zhcet.service.core.FacultyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class FloatCourseController {

    private final FacultyService facultyService;
    private final CourseManagementService courseManagementService;

    @Autowired
    public FloatCourseController(FacultyService facultyService, CourseManagementService courseManagementService) {
        this.facultyService = facultyService;
        this.courseManagementService = courseManagementService;
    }

    private void verifyCoursePermission(Course course, Department department) {
        if (course == null || !course.getDepartment().equals(department))
            throw new AccessDeniedException("403");
    }

    private void verifyCoursePermission(Course course) {
        Department department = facultyService.getFacultyDepartment();
        verifyCoursePermission(course, department);
    }

    private void verifyCoursePermission(List<Course> courses) {
        Department department = facultyService.getFacultyDepartment();
        for (Course course : courses)
            verifyCoursePermission(course, department);
    }

    @GetMapping("department/courses/{id}/float")
    public String floatCourse(RedirectAttributes redirectAttributes, @PathVariable String id) {
        Course course = courseManagementService.getCourseByCode(id);
        verifyCoursePermission(course);
        FloatedCourse floatedCourse = courseManagementService.getFloatedCourseByCode(id);

        if (floatedCourse != null) {
            log.warn("Course is already floated {}", id);
            redirectAttributes.addFlashAttribute("float_error", "Course is already floated");
        }  else {
            redirectAttributes.addFlashAttribute("courses", Collections.singletonList(course));
        }

        return "redirect:/department/course/float";
    }

    @GetMapping("/department/course/float")
    public String floatCourse(Model model) {
        FacultyMember facultyMember = facultyService.getLoggedInMember();
        Department department = FacultyService.getDepartment(facultyMember);

        model.addAttribute("page_title", "Float Course : " + facultyService.getFacultyDepartment().getName() + " Department");
        model.addAttribute("page_subtitle", "Floated Course Management");
        model.addAttribute("page_description", "Float and manage course and faculty in-charge for this session");
        model.addAttribute("department", department);

        return "department/float_course";
    }

    @PostMapping("/department/course/float")
    public String floatCourses(RedirectAttributes redirectAttributes, @RequestParam("code") List<String> codes) {
        String redirectLink = "redirect:/department/float";
        List<Course> courses = courseManagementService.getAllActiveCourse(facultyService.getFacultyDepartment(), true);

        verifyCoursePermission(courses);

        if (!courses.stream().map(Course::getCode).collect(Collectors.toList()).containsAll(codes)) {
            log.warn("Has an invalid Course Codes : Floating {}", codes.toString());
            redirectAttributes.addFlashAttribute("float_error", "Invalid Course Code!");
            return redirectLink;
        }

        List<FloatedCourse> floatedCourses = courseManagementService.getCurrentFloatedCourses(facultyService.getFacultyDepartment());

        if (floatedCourses.stream().map(floatedCourse -> floatedCourse.getCourse().getCode()).anyMatch(codes::contains)) {
            log.warn("Some courses already floated : {}", codes.toString());
            redirectAttributes.addFlashAttribute("float_error", "Some courses are already floated!");
            return redirectLink;
        }

        List<Course> courseList = courses.stream()
                .filter(course -> codes.contains(course.getCode()))
                .collect(Collectors.toList());

        for (Course course : courseList)
            courseManagementService.floatCourse(course);

        redirectAttributes.addFlashAttribute("float_success", "Courses floated successfully!");

        if (courseList.size() == 1)
            return String.format("redirect:/department/floated/%s", courseList.get(0).getCode());

        return "redirect:/department/course/float";
    }

}
