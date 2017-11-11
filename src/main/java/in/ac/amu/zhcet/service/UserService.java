package in.ac.amu.zhcet.service;

import com.google.common.base.Strings;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.data.model.user.UserDetail;
import in.ac.amu.zhcet.data.repository.UserDetailRepository;
import in.ac.amu.zhcet.data.repository.UserRepository;
import in.ac.amu.zhcet.data.type.Roles;
import in.ac.amu.zhcet.utils.Utils;
import in.ac.amu.zhcet.utils.exception.DuplicateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserDetailRepository userDetailRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userDetailRepository = userDetailRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void save(UserAuth userAuth) {
        userRepository.save(userAuth);
    }

    @Transactional
    public void save(List<UserAuth> userAuths) {
        userDetailRepository.save(
                userAuths.parallelStream()
                    .map(UserAuth::getDetails)
                .collect(Collectors.toList())
        );
        userRepository.save(userAuths);
    }

    public UserAuth findById(String id) {
        return userRepository.findByUserId(id);
    }

    public UserAuth getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    boolean throwDuplicateEmail(String email, UserAuth userAuth) {
        if (!Strings.isNullOrEmpty(email)) {
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
        List<String> roles = user.getRoles();

        if (roles.contains(Roles.SUPER_ADMIN))
            return "Super Admin";
        else if (roles.contains(Roles.DEAN_ADMIN))
            return "Dean Admin";
        else if (roles.contains(Roles.MANAGEMENT_ADMIN))
            return "Management Admin";
        else if (roles.contains(Roles.DEPARTMENT_SUPER_ADMIN))
            return "Department Super Admin";
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

    public static Stream<UserAuth> verifiedUsers(Stream<UserAuth> users) {
        return users
                .filter(userAuth -> userAuth.isEmailVerified() && !userAuth.isEmailUnsubscribed());
    }

    @Transactional
    public void updateDetails(UserAuth user, UserDetail userDetail) {
        UserDetail details = user.getDetails();
        details.setDescription(userDetail.getDescription());
        details.setAddress(userDetail.getAddress());
        details.setCity(userDetail.getCity());
        details.setState(userDetail.getState());
        details.setPhoneNumbers(userDetail.getPhoneNumbers());
        details.setDob(userDetail.getDob());

        save(user);
    }

    @Transactional
    public void changeUserPassword(UserAuth userAuth, String password) {
        userAuth.setPassword(passwordEncoder.encode(password));
        userAuth.setPasswordChanged(true);
        userRepository.save(userAuth);
    }

    @Transactional
    public void unsubscribeEmail(UserAuth userAuth, boolean unsubscribe) {
        userAuth.setEmailUnsubscribed(unsubscribe);
        userRepository.save(userAuth);
    }
}
