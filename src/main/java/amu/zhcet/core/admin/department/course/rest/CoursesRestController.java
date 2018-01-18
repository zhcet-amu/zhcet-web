package amu.zhcet.core.admin.department.course.rest;

import amu.zhcet.data.course.CourseService;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.course.floated.FloatedCourseService;
import amu.zhcet.data.department.Department;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CoursesRestController {

    private final ModelMapper modelMapper;
    private final CourseService courseService;
    private final FloatedCourseService floatedCourseService;

    @Autowired
    public CoursesRestController(ModelMapper modelMapper, CourseService courseService, FloatedCourseService floatedCourseService) {
        this.modelMapper = modelMapper;
        this.courseService = courseService;
        this.floatedCourseService = floatedCourseService;
    }

    private CourseDto attachStatus(CourseDto courseDto, List<FloatedCourse> floatedCourses) {
        courseDto.setFloated(floatedCourses.stream().map(FloatedCourse::getCourse).anyMatch(course -> course.getCode().equals(courseDto.getCode())));
        return courseDto;
    }

    @GetMapping("/admin/department/{department}/api/courses")
    public List<CourseDto> courses(@PathVariable Department department) {
        if (department == null)
            return null;

        List<FloatedCourse> floatedCourses = floatedCourseService.getCurrentFloatedCourses(department);
        return courseService.getAllActiveCourse(department, true)
                .stream()
                .map(course -> modelMapper.map(course, CourseDto.class))
                .map(courseDto -> attachStatus(courseDto, floatedCourses))
                .collect(Collectors.toList());
    }

}
