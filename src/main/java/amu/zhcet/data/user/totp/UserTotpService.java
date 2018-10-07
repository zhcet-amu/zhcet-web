package amu.zhcet.data.user.totp;

import amu.zhcet.auth.Auditor;
import amu.zhcet.data.user.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserTotpService {

    private final UserTotpRepository userTotpRepository;

    @Autowired
    public UserTotpService(UserTotpRepository userTotpRepository) {
        this.userTotpRepository = userTotpRepository;
    }

    public Optional<UserTotp> getTotpDetailsByUsername(String username) {
        return userTotpRepository.findById(username);
    }

    public Optional<UserTotp> getLoggedInTotpDetailsOptional() {
        Optional<String> usernameOptional = Auditor.getLoggedInUsernameOptional();

        if (usernameOptional.isPresent()) {
            return getTotpDetailsByUsername(usernameOptional.get());
        } else {
            throw new UserNotFoundException();
        }
    }

    public UserTotp getLoggedInTotpDetails() {
        Optional<UserTotp> totpOptional = getLoggedInTotpDetailsOptional();

        return totpOptional.orElseGet(() -> UserTotp.builder().userId(Auditor.getLoggedInUsername()).build());
    }

    public void save(UserTotp userTotp) {
        userTotpRepository.save(userTotp);
    }
}
