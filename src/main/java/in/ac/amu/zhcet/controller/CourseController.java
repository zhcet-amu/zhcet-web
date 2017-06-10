package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CourseController {

    @Autowired
    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @RequestMapping("/courses")
    public String getCourses(ModelMap modelMap) {
        modelMap.put("courses", courseRepository.findAll());
        return "courses";
    }
}
