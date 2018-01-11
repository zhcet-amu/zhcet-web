package amu.zhcet.data.user;

import amu.zhcet.common.error.DuplicateException;
import amu.zhcet.common.utils.StringUtils;
import amu.zhcet.common.utils.Utils;
import amu.zhcet.data.user.detail.UserDetail;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static void sanitizeUser(User user) {
        user.setName(StringUtils.capitalizeFirst(user.getName()));
        if (user.getEmail() != null)
            user.setEmail(Strings.emptyToNull(user.getEmail().trim().toLowerCase()));
    }

    @Transactional
    public void save(User user) {
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

    public boolean throwDuplicateEmail(String email, User user) {
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

        if (roles.contains(Role.SUPER_ADMIN))
            return "Super Admin";
        else if (roles.contains(Role.DEAN_ADMIN))
            return "Dean Admin";
        else if (roles.contains(Role.DEVELOPMENT_ADMIN))
            return "Management Admin";
        else if (roles.contains(Role.DEPARTMENT_SUPER_ADMIN))
            return "Department Super Admin";
        else if (roles.contains(Role.DEPARTMENT_ADMIN))
            return "Department Admin";
        else if (roles.contains(Role.FACULTY))
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
    public void unsubscribeEmail(User user, boolean unsubscribe) {
        user.setEmailUnsubscribed(unsubscribe);
        userRepository.save(user);
    }

    public List<UserRepository.Identifier> getUserIdentifiers(List<String> ids) {
        return userRepository.getByUserIdIn(ids);
    }

}
