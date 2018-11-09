package amu.zhcet.core.admin.dean.edit;

import amu.zhcet.data.department.Department;
import amu.zhcet.data.department.DepartmentService;
import amu.zhcet.data.user.UserService;
import amu.zhcet.data.user.faculty.FacultyMember;
import amu.zhcet.data.user.faculty.FacultyService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
class FacultyEditService {

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
        FacultyEditModel facultyEditModel = modelMapper.map(facultyMember, FacultyEditModel.class);
        facultyEditModel.setHasTotpSecret(facultyMember.getUser().hasTotpSecret());
        return facultyEditModel;
    }

    @Transactional
    public void saveFacultyMember(FacultyMember facultyMember, FacultyEditModel facultyEditModel) {
        String departmentName = facultyEditModel.getUserDepartmentName();
        Department department = ModelEditUtils.verifyDepartment(departmentName, departmentService::findByName);

        facultyEditModel.setUserEmail(ModelEditUtils.verifyNewEmail(
                facultyMember::getUser,
                facultyEditModel::getUserEmail,
                userService::checkDuplicateEmail
        ));

        facultyMember.getUser().setDepartment(department);
        modelMapper.map(facultyEditModel, facultyMember);

        if (facultyMember.getUser().getTotpDetails() != null && facultyMember.getUser().getTotpDetails().getUserId() == null) {
            // The TOTP details are detached, hence we should not save the TOTP model
            facultyMember.getUser().setTotpDetails(null);
        }

        facultyService.save(facultyMember);
    }

}
