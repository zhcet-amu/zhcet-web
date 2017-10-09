package in.ac.amu.zhcet.service.core;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.dto.datatables.FacultyEditModel;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class FacultyEditService {

    private final ModelMapper modelMapper;
    private final UserService userService;
    private final FacultyService facultyService;
    private final DepartmentService departmentService;

    public FacultyEditService(ModelMapper modelMapper, UserService userService, FacultyService facultyService, DepartmentService departmentService) {
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.facultyService = facultyService;
        this.departmentService = departmentService;
    }

    public FacultyEditModel fromFaculty(FacultyMember facultyMember) {
        return modelMapper.map(facultyMember, FacultyEditModel.class);
    }

    @Transactional
    public void saveFacultyMember(String id, FacultyEditModel facultyEditModel) {
        FacultyMember facultyMember = facultyService.getById(id);

        if (facultyMember == null)
            throw new UsernameNotFoundException("Invalid Request");

        Department department = departmentService.findByName(facultyEditModel.getUserDepartmentName());
        if (department == null)
            throw new RuntimeException("No such department :" + facultyEditModel.getUserDepartmentName());

        if (userService.throwDuplicateEmail(facultyEditModel.getUserEmail(), facultyMember.getUser()))
            facultyEditModel.setUserEmail(null);

        facultyMember.getUser().setDepartment(department);
        modelMapper.map(facultyEditModel, facultyMember);
        facultyService.save(facultyMember);
    }

}
