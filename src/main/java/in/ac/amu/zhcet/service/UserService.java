package in.ac.amu.zhcet.service;

import com.google.common.base.Strings;
import in.ac.amu.zhcet.data.model.user.User;
import in.ac.amu.zhcet.data.model.user.UserDetail;
import in.ac.amu.zhcet.data.repository.UserRepository;
import in.ac.amu.zhcet.data.type.Roles;
import in.ac.amu.zhcet.service.user.UserDetailService;
import in.ac.amu.zhcet.utils.StringUtils;
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
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    static void sanitizeUser(User user) {
        user.setName(StringUtils.capitalizeFirst(user.getName()));
        if (user.getEmail() != null)
            user.setEmail(Strings.emptyToNull(user.getEmail().trim().toLowerCase()));
    }

    @Transactional
    public void save(User user) {
        sanitizeUser(user);
        userRepository.save(user);
    }

    public Optional<User> findById(String id) {
        return userRepository.findByUserId(id);
    }

    public Optional<User> getUserByEmail(String email) {
        if (Strings.isNullOrEmpty(email))
            return Optional.empty();
        return userRepository.findByEmail(email);
    }

    boolean throwDuplicateEmail(String email, User user) {
        Optional<User> checkEmailDuplicate = getUserByEmail(email);
        if (checkEmailDuplicate.isPresent() && !checkEmailDuplicate.get().getUserId().equals(user.getUserId())) {
            log.error("User with email already exists {} {}", user.getUserId(), email);
            throw new DuplicateException("User", "email", email);
        }

        if (!Utils.isValidEmail(email)) {
            log.error("Invalid Email {} {}", user.getUserId(), email);
            throw new RuntimeException("Invalid Email");
        }

        return false;
    }

    public Optional<User> getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            return Optional.empty();

        String userName = authentication.getName();
        return findById(userName);
    }

    public String getType(User user) {
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

    public Iterable<User> getAll() {
        return userRepository.findAll(new PageRequest(0, 10, Sort.Direction.DESC, "createdAt"));
    }

    public static Stream<User> verifiedUsers(Stream<User> users) {
        return users
                .filter(user -> user.isEmailVerified() && !user.isEmailUnsubscribed());
    }

    @Transactional
    public void updateDetails(User user, UserDetail userDetail) {
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
    public void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordChanged(true);
        UserDetailService.updateStaticPrincipal(user);
        userRepository.save(user);
    }

    @Transactional
    public void unsubscribeEmail(User user, boolean unsubscribe) {
        user.setEmailUnsubscribed(unsubscribe);
        userRepository.save(user);
    }

    public List<UserRepository.Identifier> getUserIdentifiers(List<String> ids) {
        return userRepository.getByUserIdIn(ids);
    }

}
