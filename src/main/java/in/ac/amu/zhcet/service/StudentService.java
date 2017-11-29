package in.ac.amu.zhcet.service;

import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.user.UserType;
import in.ac.amu.zhcet.data.model.user.User;
import in.ac.amu.zhcet.data.repository.StudentRepository;
import in.ac.amu.zhcet.data.type.Roles;
import in.ac.amu.zhcet.service.realtime.RealTimeStatus;
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

    public Student getLoggedInStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        return getByEnrolmentNumber(userName);
    }

    public Student getByEnrolmentNumber(String userId) {
        return studentRepository.getByEnrolmentNumber(userId);
    }

    public List<StudentRepository.Identifier> getAllIdentifiers() {
        return studentRepository.findAllProjectedBy();
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

    public Student getByFacultyNumber(String userId) {
        return studentRepository.getByFacultyNumber(userId);
    }

    private Student initializeStudent(Student student) {
        student.getUser().setType(UserType.STUDENT);

        if (student.getUser().getUserId() == null)
            student.getUser().setUserId(student.getEnrolmentNumber());
        if (student.getUser().getRoles() == null || student.getUser().getRoles().isEmpty())
            student.getUser().setRoles(Collections.singleton(Roles.STUDENT));
        if (student.getUser().getPassword() == null)
            student.getUser().setPassword(student.getFacultyNumber());

        student.getUser().setPassword(passwordEncoder.encode(student.getUser().getPassword()));

        userService.save(student.getUser());

        return student;
    }

    @Async
    public void register(Set<Student> students, RealTimeStatus status) {
        long startTime = System.currentTimeMillis();
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
            float duration = (System.currentTimeMillis() - startTime)/1000f;
            status.setDuration(duration);
            status.setFinished(true);
            log.info("Saved {} Students in {} s", students.size(), duration);
        } catch (Exception exception) {
            log.error("Error while saving students", exception);
            status.setMessage(exception.getMessage());
            status.setFailed(true);
            throw exception;
        }
    }

    @Transactional
    public void save(Student student) {
        studentRepository.save(student);
    }

}
