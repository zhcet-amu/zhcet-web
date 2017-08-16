package in.ac.amu.zhcet.data.service;

import in.ac.amu.zhcet.data.Roles;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.base.user.Type;
import in.ac.amu.zhcet.data.model.base.user.UserAuth;
import in.ac.amu.zhcet.data.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student getLoggedInStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        return getByEnrolmentNumber(userName);
    }

    public Student getByEnrolmentNumber(String userId) {
        return studentRepository.getByEnrolmentNumber(userId);
    }

    public Student getByFacultyNumber(String userId) {
        return studentRepository.getByFacultyNumber(userId);
    }

    private static void initializeStudent(Student student) {
        student.getUser().setType(Type.STUDENT);

        if (student.getUser().getRoles() == null || student.getUser().getRoles().length == 0)
            student.getUser().setRoles(new String[] { Roles.STUDENT });
        if (student.getUser().getPassword() == null)
            student.getUser().setPassword(student.getFacultyNumber());

        student.getUser().setPassword(UserAuth.PASSWORD_ENCODER.encode(student.getUser().getPassword()));
    }

    @Transactional
    public void register(Student student) {
        initializeStudent(student);

        studentRepository.save(student);
    }

}
