package in.ac.amu.zhcet.controller.dean;

import com.fasterxml.jackson.annotation.JsonView;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.StudentView;
import in.ac.amu.zhcet.data.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
public class DeanRestController {

    private final ModelMapper modelMapper;
    private final StudentRepository studentRepository;

    private static Map<String, String> propertyMap = new ConcurrentHashMap<>();

    static {
        propertyMap.put("enrolment-number", "enrolmentNumber");
        propertyMap.put("faculty-number", "facultyNumber");
        propertyMap.put("name", "user.name");
        propertyMap.put("email", "user.email");
        propertyMap.put("department", "user.details.department.name");
    }

    @Autowired
    public DeanRestController(ModelMapper modelMapper, StudentRepository studentRepository) {
        this.modelMapper = modelMapper;
        this.studentRepository = studentRepository;
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/dean/users", method = RequestMethod.POST)
    public DataTablesOutput<StudentView> getUsers(@Valid @RequestBody DataTablesInput input) {
        convertInput(input);
        return studentRepository.findAll(input, this::fromStudent);
    }

    private static void convertInput(DataTablesInput input) {
        input.getColumns().replaceAll(column -> {
            String mapped = propertyMap.get(column.getData());
            if (mapped != null) column.setData(mapped);
            return column;
        });
    }

    private StudentView fromStudent(Student student) {
        return modelMapper.map(student, StudentView.class);
    }

}
