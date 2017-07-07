package in.ac.amu.zhcet.data.service;

import in.ac.amu.zhcet.data.model.BaseUser;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.repository.StudentRepository;
import in.ac.amu.zhcet.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository, UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    public Student getLoggedInStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        Student student = getByUserId(userName);
        if (student == null) {
            BaseUser user = userRepository.findByUserId(userName);
            student = new Student(user, null);
        }

        return student;
    }

    public Student getByUserId(String userId) {
        return studentRepository.getByUser_userId(userId);
    }

    @Transactional
    public void updateStudent(Student student) {
        userRepository.save(student.getUser());
        studentRepository.save(student);
    }

}
