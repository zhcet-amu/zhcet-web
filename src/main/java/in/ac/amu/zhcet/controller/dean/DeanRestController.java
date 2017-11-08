package in.ac.amu.zhcet.controller.dean;

import com.fasterxml.jackson.annotation.JsonView;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.datatables.FacultyView;
import in.ac.amu.zhcet.data.model.dto.datatables.FloatedCourseView;
import in.ac.amu.zhcet.data.model.dto.datatables.StudentView;
import in.ac.amu.zhcet.data.repository.FacultyRepository;
import in.ac.amu.zhcet.data.repository.FloatedCourseRepository;
import in.ac.amu.zhcet.data.repository.StudentRepository;
import in.ac.amu.zhcet.service.CourseInChargeService;
import in.ac.amu.zhcet.service.ConfigurationService;
import in.ac.amu.zhcet.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.Column;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
public class DeanRestController {

    private final ModelMapper modelMapper;
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;
    private final FloatedCourseRepository floatedCourseRepository;
    private final CourseInChargeService courseInChargeService;

    @Autowired
    public DeanRestController(ModelMapper modelMapper, FacultyRepository facultyRepository, StudentRepository studentRepository, FloatedCourseRepository floatedCourseRepository, CourseInChargeService courseInChargeService) {
        this.modelMapper = modelMapper;
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
        this.floatedCourseRepository = floatedCourseRepository;
        this.courseInChargeService = courseInChargeService;
    }

    @JsonView(DataTablesOutput.View.class)
    @PostMapping(value = "/dean/api/students")
    public DataTablesOutput<StudentView> getStudents(@Valid @RequestBody DataTablesInput input) {
        convertInput(input);
        return studentRepository.findAll(input, this::fromStudent);
    }

    @JsonView(DataTablesOutput.View.class)
    @PostMapping(value = "/dean/api/faculty")
    public DataTablesOutput<FacultyView> getFaculty(@Valid @RequestBody DataTablesInput input) {
        convertInput(input);
        Boolean working = sanitizeBoolean(input, "working");
        return facultyRepository.findAll(input, (root, query, cb) ->
                        (working != null) ? cb.equal(root.get("working"), working) : cb.and(), // cb.and() is always true
                null, this::fromFaculty);
    }

    @JsonView(DataTablesOutput.View.class)
    @PostMapping(value = "/dean/api/floated")
    public DataTablesOutput<FloatedCourseView> getFloatedCourses(@Valid @RequestBody DataTablesInput input) {
        convertInput(input);

        return floatedCourseRepository.findAll(input, (root, query, cb) ->
                cb.equal(root.get("session"), ConfigurationService.getDefaultSessionCode()),
                null, this::fromFloatedCourse);
    }

    /**
     * Workaround for boolean column filtering as default library version does not work correctly
     * @param input DataTablesInput : Input to be sanitized and source of the boolean to be returned
     * @param columnName String : Boolean column name to be cleared
     * @return Boolean : Set value of columnName before sanitizing
     */
    private static Boolean sanitizeBoolean(DataTablesInput input, String columnName) {
        Optional<Column> columnOptional = input.getColumns()
                .stream()
                .filter(column -> column.getName().equals(columnName))
                .findFirst();

        if (!columnOptional.isPresent())
            return null;

        Column column = columnOptional.get();
        String value = column.getSearch().getValue();
        Boolean stored = StringUtils.isEmpty(value) ? null : Boolean.parseBoolean(value);
        column.getSearch().setValue(null);

        return stored;
    }

    private static void convertInput(DataTablesInput input) {
        input.getColumns().replaceAll(column -> {
            column.setData(column.getData().replace('_', '.'));
            return column;
        });
    }

    private StudentView fromStudent(Student student) {
        return modelMapper.map(student, StudentView.class);
    }

    private FacultyView fromFaculty(FacultyMember facultyMember) {
        return modelMapper.map(facultyMember, FacultyView.class);
    }

    private FloatedCourseView fromFloatedCourse(FloatedCourse floatedCourse) {
        FloatedCourseView floatedCourseView = modelMapper.map(floatedCourse, FloatedCourseView.class);
        floatedCourseView.setNumStudents(floatedCourse.getCourseRegistrations().size());
        floatedCourseView.setSections(courseInChargeService.getSections(floatedCourse).toString());

        return floatedCourseView;
    }

}
