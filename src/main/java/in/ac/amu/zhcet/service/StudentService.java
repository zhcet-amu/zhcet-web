package in.ac.amu.zhcet.service;

import in.ac.amu.zhcet.data.type.Roles;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.user.Type;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.data.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
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

    public List<Student> getByIds(List<String> studentIds) {
        return studentRepository.getByEnrolmentNumberIn(studentIds);
    }

    public Student getByFacultyNumber(String userId) {
        return studentRepository.getByFacultyNumber(userId);
    }

    private Student initializeStudent(Student student) {
        student.getUser().setType(Type.STUDENT);

        if (student.getUser().getUserId() == null)
            student.getUser().setUserId(student.getEnrolmentNumber());
        if (student.getUser().getRoles() == null || student.getUser().getRoles().isEmpty())
            student.getUser().setRoles(Collections.singleton(Roles.STUDENT));
        if (student.getUser().getPassword() == null)
            student.getUser().setPassword(student.getFacultyNumber());

        student.getUser().setPassword(passwordEncoder.encode(student.getUser().getPassword()));

        return student;
    }

    @Transactional
    public void register(Set<Student> students) {
        List<Student> studentList = students.parallelStream()
                .map(this::initializeStudent)
                .collect(Collectors.toList());
        List<UserAuth> userAuths = studentList.parallelStream()
                .map(Student::getUser)
                .collect(Collectors.toList());
        userService.save(userAuths);
        studentRepository.save(studentList);
        log.info("Saved Students");
    }

    @Transactional
    public void save(Student student) {
        studentRepository.save(student);
    }

}
