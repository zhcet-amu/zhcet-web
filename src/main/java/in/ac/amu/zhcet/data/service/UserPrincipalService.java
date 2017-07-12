package in.ac.amu.zhcet.data.service;

import in.ac.amu.zhcet.data.model.base.user.UserAuth;
import in.ac.amu.zhcet.data.model.base.user.UserPrincipal;
import in.ac.amu.zhcet.data.repository.FacultyRepository;
import in.ac.amu.zhcet.data.repository.StudentRepository;
import in.ac.amu.zhcet.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserPrincipalService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    @Autowired
    public UserPrincipalService(UserRepository userRepository, StudentRepository studentRepository, FacultyRepository facultyRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    public UserPrincipal findById(String id) {
        UserAuth user = userRepository.findByUserId(id);

        if (user == null)
            return null;

        switch (user.getType()) {
            case "STUDENT":
                return studentRepository.getByEnrolmentNumber(id);
            case "FACULTY":
                return facultyRepository.getByFacultyId(id);
            default:
                return null;
        }
    }

}
