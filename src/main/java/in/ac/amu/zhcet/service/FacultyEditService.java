package in.ac.amu.zhcet.service;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.dto.datatables.FacultyEditModel;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
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
        Optional<FacultyMember> facultyMemberOptional = facultyService.getById(id);
        facultyMemberOptional.orElseThrow(() -> {
            log.error("Tried saving non-existent faculty member {}", id);
            return new UsernameNotFoundException("Invalid Request");
        });

        facultyMemberOptional.ifPresent(facultyMember -> {
            String departmentName = facultyEditModel.getUserDepartmentName();
            Optional<Department> departmentOptional = departmentService.findByName(departmentName);
            departmentOptional.orElseThrow(() -> {
                log.error("Tried saving faculty with non-existent department {} {}", id, departmentName);
                return new RuntimeException("No such department : " + departmentName);
            });

            departmentOptional.ifPresent(department -> {
                if (!facultyEditModel.getUserEmail().equals(facultyMember.getUser().getEmail())) {
                    if (userService.throwDuplicateEmail(facultyEditModel.getUserEmail(), facultyMember.getUser()))
                        facultyEditModel.setUserEmail(null);
                    facultyMember.getUser().setEmailVerified(false);
                }

                facultyMember.getUser().setDepartment(department);
                modelMapper.map(facultyEditModel, facultyMember);
                facultyService.save(facultyMember);
            });
        });
    }

}
