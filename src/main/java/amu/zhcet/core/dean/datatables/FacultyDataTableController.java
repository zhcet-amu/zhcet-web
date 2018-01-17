package amu.zhcet.core.dean.datatables;

import amu.zhcet.data.user.faculty.FacultyMember;
import amu.zhcet.data.user.faculty.FacultyRepository;
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
public class FacultyDataTableController {

    private final ModelMapper modelMapper;
    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyDataTableController(ModelMapper modelMapper, FacultyRepository facultyRepository) {
        this.modelMapper = modelMapper;
        this.facultyRepository = facultyRepository;
    }

    @JsonView(DataTablesOutput.View.class)
    @PostMapping(value = "/admin/dean/api/faculty")
    public DataTablesOutput<FacultyView> getFaculty(@Valid @RequestBody DataTablesInput input) {
        DataTableUtils.convertInput(input);
        Boolean working = DataTableUtils.sanitizeBoolean(input, "working");
        return facultyRepository.findAll(input, (root, query, cb) ->
                        (working != null) ? cb.equal(root.get("working"), working) : cb.and(), // cb.and() is always true
                null, this::fromFaculty);
    }

    private FacultyView fromFaculty(FacultyMember facultyMember) {
        return modelMapper.map(facultyMember, FacultyView.class);
    }

}
