package in.ac.amu.zhcet.service.core;

import in.ac.amu.zhcet.data.Roles;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.user.Type;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.data.repository.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final UserService userService;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository, UserService userService) {
        this.facultyRepository = facultyRepository;
        this.userService = userService;
    }

    public static Department getDepartment(FacultyMember facultyMember) {
        return facultyMember.getUser().getDetails().getDepartment();
    }

    public FacultyMember getById(String facultyId) {
        return facultyRepository.getByFacultyId(facultyId);
    }

    public List<FacultyMember> getByIds(List<String> facultyIds) {
        return facultyRepository.getByFacultyIdIn(facultyIds);
    }

    public List<FacultyMember> getByDepartment(Department department) {
        return facultyRepository.getByUser_Details_Department(department);
    }

    public FacultyMember getLoggedInMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        return getById(userName);
    }

    private static void initializeFaculty(FacultyMember facultyMember) {
        facultyMember.getUser().setType(Type.FACULTY);

        if (facultyMember.getUser().getUserId() == null)
            facultyMember.getUser().setUserId(facultyMember.getFacultyId());
        if (facultyMember.getUser().getRoles() == null || facultyMember.getUser().getRoles().length == 0)
            facultyMember.getUser().setRoles(new String[]{ Roles.FACULTY });

        facultyMember.getUser().setPassword(UserAuth.PASSWORD_ENCODER.encode(facultyMember.getUser().getPassword()));
    }

    @Transactional
    public void register(FacultyMember facultyMember) {
        initializeFaculty(facultyMember);
        userService.save(facultyMember.getUser());
        facultyRepository.save(facultyMember);
    }

    @Transactional
    public void save(FacultyMember facultyMember) {
        facultyRepository.save(facultyMember);
    }

}
