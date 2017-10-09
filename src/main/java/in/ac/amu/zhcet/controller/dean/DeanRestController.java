package in.ac.amu.zhcet.controller.dean;

import com.fasterxml.jackson.annotation.JsonView;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.datatables.FacultyView;
import in.ac.amu.zhcet.data.model.dto.datatables.StudentView;
import in.ac.amu.zhcet.data.repository.FacultyRepository;
import in.ac.amu.zhcet.data.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
public class DeanRestController {

    private final ModelMapper modelMapper;
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public DeanRestController(ModelMapper modelMapper, FacultyRepository facultyRepository, StudentRepository studentRepository) {
        this.modelMapper = modelMapper;
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
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
        return facultyRepository.findAll(input, this::fromFaculty);
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

}
