package amu.zhcet.data.user.faculty;

import amu.zhcet.common.utils.StringUtils;
import amu.zhcet.data.department.Department;
import amu.zhcet.data.user.Role;
import amu.zhcet.data.user.UserService;
import amu.zhcet.data.user.UserType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository, UserService userService, PasswordEncoder passwordEncoder) {
        this.facultyRepository = facultyRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<FacultyMember> getById(String facultyId) {
        return facultyRepository.getByFacultyId(facultyId);
    }

    public List<FacultyMember> getAll() {
        return facultyRepository.findAllByWorking(true);
    }

    public List<FacultyMember> getByDepartment(Department department) {
        return facultyRepository.getByUser_DepartmentAndWorking(department, true);
    }

    public List<FacultyMember> getAllByDepartment(Department department) {
        return facultyRepository.getByUser_Department(department);
    }

    public Optional<FacultyMember> getLoggedInMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        return getById(userName);
    }

    private FacultyMember initializeFaculty(FacultyMember facultyMember) {
        facultyMember.getUser().setType(UserType.FACULTY);

        if (facultyMember.getUser().getUserId() == null)
            facultyMember.getUser().setUserId(facultyMember.getFacultyId());
        if (facultyMember.getUser().getRoles() == null || facultyMember.getUser().getRoles().isEmpty())
            facultyMember.getUser().setRoles(Collections.singleton(Role.FACULTY.toString()));

        facultyMember.getUser().setPassword(passwordEncoder.encode(facultyMember.getUser().getPassword()));

        return facultyMember;
    }

    private static void sanitizeFaculty(FacultyMember facultyMember) {
        UserService.sanitizeUser(facultyMember.getUser());
        facultyMember.setFacultyId(StringUtils.capitalizeAll(facultyMember.getFacultyId()));
        facultyMember.setDesignation(StringUtils.capitalizeFirst(facultyMember.getDesignation()));
    }

    @Transactional
    public void register(FacultyMember facultyMember) {
        sanitizeFaculty(initializeFaculty(facultyMember));
        userService.save(facultyMember.getUser());
        facultyRepository.save(facultyMember);
    }

    @Transactional
    public void save(FacultyMember facultyMember) {
        facultyRepository.save(facultyMember);
    }

}
