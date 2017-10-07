package in.ac.amu.zhcet.controller.department;

import in.ac.amu.zhcet.service.core.CourseManagementService;
import in.ac.amu.zhcet.service.core.FacultyService;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CoursesRestController {

    private final ModelMapper modelMapper;
    private final FacultyService facultyService;
    private final CourseManagementService courseManagementService;

    @Autowired
    public CoursesRestController(ModelMapper modelMapper, FacultyService facultyService, CourseManagementService courseManagementService) {
        this.modelMapper = modelMapper;
        this.facultyService = facultyService;
        this.courseManagementService = courseManagementService;
    }

    @Data
    private static class CourseDto {
        private String code;
        private String title;
    }

    @GetMapping("/department/api/courses")
    public List<CourseDto> courses() {
        return courseManagementService.getAllActiveCourse(facultyService.getFacultyDepartment(), true)
                .stream()
                .map(course ->
                        modelMapper
                        .map(course, CourseDto.class))
                .collect(Collectors.toList());
    }

}
