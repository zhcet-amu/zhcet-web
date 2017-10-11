package in.ac.amu.zhcet.service.core;

import in.ac.amu.zhcet.data.Roles;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.data.repository.UserRepository;
import in.ac.amu.zhcet.utils.DuplicateException;
import in.ac.amu.zhcet.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(UserAuth userAuth) {
        userRepository.save(userAuth);
    }

    public void save(List<UserAuth> userAuths) {
        userRepository.save(userAuths);
    }

    public UserAuth findById(String id) {
        return userRepository.findByUserId(id);
    }

    public UserAuth getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    // Checking if the user is active prevents email hoarding
    public boolean emailExists(String email) {
        return getUserByEmail(email) != null;
    }

    public boolean throwDuplicateEmail(String email, UserAuth userAuth) {
        if (!Utils.isEmpty(email)) {
            UserAuth checkEmailDuplicate = getUserByEmail(email);
            if (checkEmailDuplicate != null && !checkEmailDuplicate.getUserId().equals(userAuth.getUserId())) {
                log.error("User with email already exists {} {}", userAuth.getUserId(), email);
                throw new DuplicateException("User", "email", email);
            }
            if (!Utils.isValidEmail(email)) {
                log.error("Invalid Email {} {}", userAuth.getUserId(), email);
                throw new RuntimeException("Invalid Email");
            }
        } else {
            return true;
        }

        return false;
    }

    public UserAuth getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            return null;

        String userName = authentication.getName();
        return findById(userName);
    }

    public String getType(UserAuth user) {
        List<String> roles = Arrays.asList(user.getRoles());

        if (roles.contains(Roles.DEAN_ADMIN))
            return "Dean Admin";
        else if (roles.contains(Roles.DEPARTMENT_ADMIN))
            return "Department Admin";
        else if (roles.contains(Roles.FACULTY))
            return "Faculty Member";
        else
            return "Student";
    }

    public Iterable<UserAuth> getAll() {
        return userRepository.findAll(new PageRequest(0, 10, Sort.Direction.DESC, "createdAt"));
    }

    @Transactional
    public void updateDepartment(String id, Department department) {
        UserAuth user = findById(id);
        user.setDepartment(department);
        userRepository.save(user);
    }

    public void changeUserPassword(UserAuth userAuth, String password) {
        userAuth.setPassword(UserAuth.PASSWORD_ENCODER.encode(password));
        userAuth.setPasswordChanged(true);
        userRepository.save(userAuth);
    }
}
