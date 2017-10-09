package in.ac.amu.zhcet.controller.department;

import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.service.core.FacultyService;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FacultyRestController {

    private final ModelMapper modelMapper;
    private final FacultyService facultyService;

    @Data
    private static class FacultyDto {
        private String facultyId;
        private String userName;
        private String userDetailsAvatarUrl;
        private String userDepartmentName;
    }

    @Autowired
    public FacultyRestController(ModelMapper modelMapper, FacultyService facultyService) {
        this.modelMapper = modelMapper;
        this.facultyService = facultyService;
    }


    @GetMapping("/department/api/faculty")
    public List<FacultyDto> faculty(@RequestParam(required = false) Boolean all) {
        List<FacultyMember> facultyMembers;

        if (all != null && all)
            facultyMembers = facultyService.getAll();
        else
            facultyMembers = facultyService.getByDepartment(facultyService.getFacultyDepartment());

        return facultyMembers
                .stream()
                .map(facultyMember -> modelMapper.map(facultyMember, FacultyDto.class))
                .collect(Collectors.toList());
    }

}
