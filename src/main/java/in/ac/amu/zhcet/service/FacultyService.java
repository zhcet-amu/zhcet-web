package in.ac.amu.zhcet.service;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.user.UserType;
import in.ac.amu.zhcet.data.repository.FacultyRepository;
import in.ac.amu.zhcet.data.type.Roles;
import in.ac.amu.zhcet.service.realtime.RealTimeStatus;
import in.ac.amu.zhcet.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
            facultyMember.getUser().setRoles(Collections.singleton(Roles.FACULTY));

        facultyMember.getUser().setPassword(passwordEncoder.encode(facultyMember.getUser().getPassword()));

        userService.save(facultyMember.getUser());

        return facultyMember;
    }

    @Async
    public void register(Set<FacultyMember> facultyMembers, RealTimeStatus status) {
        long startTime = System.nanoTime();
        status.setContext("Faculty Registration");
        status.setTotal(facultyMembers.size());

        try {
            final int[] completed = {1};
            facultyMembers.stream()
                    .map(this::initializeFaculty)
                    .forEach(facultyMember -> {
                        sanitizeFaculty(facultyMember);
                        save(facultyMember);
                        status.setCompleted(completed[0]++);
                    });
            float duration = (System.nanoTime() - startTime)/1000000f;
            status.setDuration(duration);
            status.setFinished(true);
            log.info("Saved {} Faculty in {} s", facultyMembers.size(), duration);
        } catch (Exception exception) {
            log.error("Error while saving faculty", exception);
            status.setMessage(exception.getMessage());
            status.setFailed(true);
            throw exception;
        }
    }

    private static void sanitizeFaculty(FacultyMember facultyMember) {
        UserService.sanitizeUser(facultyMember.getUser());
        facultyMember.setFacultyId(StringUtils.capitalizeAll(facultyMember.getFacultyId()));
        facultyMember.setDesignation(StringUtils.capitalizeFirst(facultyMember.getDesignation()));
    }

    @Transactional
    public void save(FacultyMember facultyMember) {
        facultyRepository.save(facultyMember);
    }

}
