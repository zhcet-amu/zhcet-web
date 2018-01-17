package amu.zhcet.core.department.rest;

import amu.zhcet.data.department.Department;
import amu.zhcet.data.user.faculty.FacultyMember;
import amu.zhcet.data.user.faculty.FacultyService;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DepartmentMembersController {

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
    public DepartmentMembersController(ModelMapper modelMapper, FacultyService facultyService) {
        this.modelMapper = modelMapper;
        this.facultyService = facultyService;
    }

    @GetMapping("/admin/department/{department}/api/faculty")
    public List<FacultyDto> faculty(@PathVariable Department department, @RequestParam(required = false) Boolean all) {
        if (department == null)
            return null;

        List<FacultyMember> facultyMembers;

        if (all != null && all)
            facultyMembers = facultyService.getAll();
        else
            facultyMembers = facultyService.getByDepartment(department);

        return facultyMembers
                .stream()
                .map(facultyMember -> modelMapper.map(facultyMember, FacultyDto.class))
                .collect(Collectors.toList());
    }

}
