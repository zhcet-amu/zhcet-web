package in.ac.amu.zhcet.data.service;

import in.ac.amu.zhcet.data.model.BaseUser;
import in.ac.amu.zhcet.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
