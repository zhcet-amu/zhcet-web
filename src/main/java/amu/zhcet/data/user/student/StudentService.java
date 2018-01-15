package amu.zhcet.data.user.student;

import amu.zhcet.common.realtime.RealTimeStatus;
import amu.zhcet.common.utils.StringUtils;
import amu.zhcet.data.user.Role;
import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserService;
import amu.zhcet.data.user.UserType;
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
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
public class StudentService {

    private final UserService userService;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public StudentService(UserService userService, StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Student> getLoggedInStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        return getByEnrolmentNumber(userName);
    }

    public Optional<Student> getByEnrolmentNumber(String userId) {
        return studentRepository.getByEnrolmentNumber(userId);
    }

    public List<StudentRepository.Identifier> getAllIdentifiers() {
        return studentRepository.findAllProjectedBy();
    }

    public List<StudentRepository.Identifier> getIdentifiersByFacultyNummbers(List<String> facultyNumbers) {
        return studentRepository.getByFacultyNumberIn(facultyNumbers);
    }

    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    public List<Student> getBySection(String section) {
        return studentRepository.getBySectionAndStatus(section, 'A');
    }

    private static Stream<User> verifiedUsers(Stream<Student> students) {
        return UserService.verifiedUsers(students.map(Student::getUser));
    }

    public static Stream<String> getEmails(Stream<Student> students) {
        return verifiedUsers(students)
                .map(User::getEmail);
    }

    public Optional<Student> getByFacultyNumber(String userId) {
        return studentRepository.getByFacultyNumber(userId);
    }

    private Student initializeStudent(Student student) {
        student.getUser().setType(UserType.STUDENT);

        if (student.getUser().getUserId() == null)
            student.getUser().setUserId(student.getEnrolmentNumber());
        if (student.getUser().getRoles() == null || student.getUser().getRoles().isEmpty())
            student.getUser().setRoles(Collections.singleton(Role.STUDENT.toString()));
        if (student.getUser().getPassword() == null)
            student.getUser().setPassword(student.getFacultyNumber());

        student.getUser().setPassword(passwordEncoder.encode(student.getUser().getPassword()));

        userService.save(student.getUser());

        return student;
    }

    // TODO: Extract to local package
    @Async
    public void register(Set<Student> students, RealTimeStatus status) {
        long startTime = System.nanoTime();
        status.setContext("Student Registration");
        status.setTotal(students.size());

        try {
            final int[] completed = {1};
            students.stream()
                    .map(this::initializeStudent)
                    .forEach(student -> {
                        save(student);
                        status.setCompleted(completed[0]++);
                    });
            float duration = (System.nanoTime() - startTime)/1000000f;
            status.setDuration(duration);
            status.setFinished(true);
            log.info("Saved {} Students in {} ms", students.size(), duration);
        } catch (Exception exception) {
            log.error("Error while saving students", exception);
            status.setMessage(exception.getMessage());
            status.setFailed(true);
            throw exception;
        }
    }

    private static void sanitizeStudent(Student student) {
        UserService.sanitizeUser(student.getUser());
        student.setEnrolmentNumber(StringUtils.capitalizeAll(student.getEnrolmentNumber()));
        student.setFacultyNumber(StringUtils.capitalizeAll(student.getFacultyNumber()));
        student.setSection(StringUtils.capitalizeAll(student.getSection()));
    }

    @Transactional
    public void save(Student student) {
        sanitizeStudent(student);
        studentRepository.save(student);
    }

}
