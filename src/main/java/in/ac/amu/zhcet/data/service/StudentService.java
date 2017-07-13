package in.ac.amu.zhcet.data.service;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.base.user.UserDetails;
import in.ac.amu.zhcet.data.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserService userService;
    private final UserDetailService userDetailService;

    @Autowired
    public StudentService(StudentRepository studentRepository, UserService userService, UserDetailService userDetailService) {
        this.studentRepository = studentRepository;
        this.userService = userService;
        this.userDetailService = userDetailService;
    }

    public Student getLoggedInStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        return getByEnrolmentNumber(userName);
    }

    public Student getByEnrolmentNumber(String userId) {
        return studentRepository.getByEnrolmentNumber(userId);
    }

    @Transactional
    public void register(Student student) {
        userService.saveUser(student.getUser());
        studentRepository.save(student);
    }

    @Transactional
    public void updateStudentDetails(String enrolmentNumber, UserDetails userDetails) {
        Student student = getByEnrolmentNumber(enrolmentNumber);
        student.setUserDetails(userDetails);
        studentRepository.save(student);

        userDetailService.updatePrincipal(student);
    }

}
