package in.ac.amu.zhcet.service;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.user.Type;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.data.repository.FacultyRepository;
import in.ac.amu.zhcet.data.type.Roles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final UserService userService;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository, UserService userService) {
        this.facultyRepository = facultyRepository;
        this.userService = userService;
    }

    public FacultyMember getById(String facultyId) {
        return facultyRepository.getByFacultyId(facultyId);
    }

    public List<FacultyMember> getAll() {
        return facultyRepository.findAllByWorking(true);
    }

    public List<FacultyMember> getByIds(List<String> facultyIds) {
        return facultyRepository.getByFacultyIdIn(facultyIds);
    }

    public List<FacultyMember> getByDepartment(Department department) {
        return facultyRepository.getByUser_DepartmentAndWorking(department, true);
    }

    public List<FacultyMember> getAllByDepartment(Department department) {
        return facultyRepository.getByUser_Department(department);
    }

    public FacultyMember getLoggedInMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        return getById(userName);
    }

    private static FacultyMember initializeFaculty(FacultyMember facultyMember) {
        facultyMember.getUser().setType(Type.FACULTY);

        if (facultyMember.getUser().getUserId() == null)
            facultyMember.getUser().setUserId(facultyMember.getFacultyId());
        if (facultyMember.getUser().getRoles() == null || facultyMember.getUser().getRoles().isEmpty())
            facultyMember.getUser().setRoles(Collections.singleton(Roles.FACULTY));

        facultyMember.getUser().setPassword(UserAuth.PASSWORD_ENCODER.encode(facultyMember.getUser().getPassword()));
        return facultyMember;
    }

    @Transactional
    public void register(Set<FacultyMember> facultyMembers) {
        List<FacultyMember> memberList = facultyMembers.parallelStream()
                .map(FacultyService::initializeFaculty)
                .collect(Collectors.toList());
        List<UserAuth> userAuths = memberList.parallelStream()
                .map(FacultyMember::getUser)
                .collect(Collectors.toList());
        userService.save(userAuths);
        facultyRepository.save(memberList);
        log.info("Saved Faculty Members");
    }

    @Transactional
    public void save(FacultyMember facultyMember) {
        facultyRepository.save(facultyMember);
    }

}
