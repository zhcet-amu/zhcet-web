package amu.zhcet.core.dean.datatables.student;

import amu.zhcet.core.dean.datatables.DataTableUtils;
import amu.zhcet.data.user.student.Student;
import amu.zhcet.data.user.student.StudentRepository;
import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class StudentDataTableController {

    private final ModelMapper modelMapper;
    private final StudentRepository studentRepository;

    @Autowired
    public StudentDataTableController(ModelMapper modelMapper, StudentRepository studentRepository) {
        this.modelMapper = modelMapper;
        this.studentRepository = studentRepository;
    }

    @JsonView(DataTablesOutput.View.class)
    @PostMapping(value = "/dean/api/students")
    public DataTablesOutput<StudentView> getStudents(@Valid @RequestBody DataTablesInput input) {
        DataTableUtils.convertInput(input);
        return studentRepository.findAll(input, this::fromStudent);
    }

    private StudentView fromStudent(Student student) {
        return modelMapper.map(student, StudentView.class);
    }

}
