package amu.zhcet.core.admin.dean.registration.course.floated;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/dean/float")
public class FloatCoursesController {

    @GetMapping
    public String floatCourse(Model model) {
        model.addAttribute("page_title", "Float Courses");
        model.addAttribute("page_subtitle", "Float courses using CSV");
        model.addAttribute("page_description", "Upload courses CSV to float courses in current session");

        return "dean/float_course";
    }

}
