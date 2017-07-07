package in.ac.amu.zhcet.data.service;

import in.ac.amu.zhcet.data.Roles;
import in.ac.amu.zhcet.data.model.base.BaseUser;
import in.ac.amu.zhcet.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(BaseUser user) {
        user.setPassword(BaseUser.PASSWORD_ENCODER.encode(user.getPassword()));
        userRepository.save(user);
    }

    public String getType(BaseUser user) {
        List<String> roles = Arrays.asList(user.getRoles());

        if (roles.contains(Roles.DEAN_ADMIN))
            return "Dean Admin";
        else if (roles.contains(Roles.FACULTY))
            return "FacultyMember";
        else
            return "Student";
    }

    public Iterable<BaseUser> getAll() {
        return userRepository.findAllByOrderByCreatedAtDesc();
    }

}
