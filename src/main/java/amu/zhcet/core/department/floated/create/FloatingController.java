package amu.zhcet.core.department.floated.create;

import amu.zhcet.core.error.ErrorUtils;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.floated.FloatedCourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

@Slf4j
@Controller
@RequestMapping("/admin/department/float/{course}")
public class FloatingController {

    private final FloatedCourseService floatedCourseService;

    @Autowired
    public FloatingController(FloatedCourseService floatedCourseService) {
        this.floatedCourseService = floatedCourseService;
    }

    @GetMapping
    public String floatCourse(@PathVariable Course course, RedirectAttributes redirectAttributes) {
        ErrorUtils.requireNonNullCourse(course);

        redirectAttributes.addAttribute("department", course.getDepartment());
        if (floatedCourseService.isFloated(course)) {
            log.warn("Course is already floated {}", course.getCode());
            redirectAttributes.addFlashAttribute("float_error", "Course is already floated");
        }  else {
            redirectAttributes.addFlashAttribute("courses", Collections.singletonList(course));
        }

        return "redirect:/admin/department/{department}/float";
    }

}
