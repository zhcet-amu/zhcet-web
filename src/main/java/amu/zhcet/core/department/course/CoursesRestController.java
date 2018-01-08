package amu.zhcet.core.department.course;

import amu.zhcet.data.course.CourseManagementService;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.department.Department;
import lombok.Data;
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
    private final CourseManagementService courseManagementService;

    @Autowired
    public CoursesRestController(ModelMapper modelMapper, CourseManagementService courseManagementService) {
        this.modelMapper = modelMapper;
        this.courseManagementService = courseManagementService;
    }

    @Data
    private static class CourseDto {
        private String code;
        private String title;
        private Integer semester;
        private String category;
        private Float credits;
        private boolean floated;
    }

    private CourseDto attachStatus(CourseDto courseDto, List<FloatedCourse> floatedCourses) {
        courseDto.setFloated(floatedCourses.stream().map(FloatedCourse::getCourse).anyMatch(course -> course.getCode().equals(courseDto.getCode())));
        return courseDto;
    }

    @GetMapping("/department/{department}/api/courses")
    public List<CourseDto> courses(@PathVariable Department department) {
        if (department == null)
            return null;

        List<FloatedCourse> floatedCourses = courseManagementService.getCurrentFloatedCourses(department);
        return courseManagementService.getAllActiveCourse(department, true)
                .stream()
                .map(course -> modelMapper.map(course, CourseDto.class))
                .map(courseDto -> attachStatus(courseDto, floatedCourses))
                .collect(Collectors.toList());
    }

}
