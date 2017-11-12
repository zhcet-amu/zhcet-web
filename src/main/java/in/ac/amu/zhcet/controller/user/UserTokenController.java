package in.ac.amu.zhcet.controller.user;

import in.ac.amu.zhcet.service.firebase.FirebaseAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserTokenController {

    private final FirebaseAuthService firebaseAuthService;

    @Autowired
    public UserTokenController(FirebaseAuthService firebaseAuthService) {
        this.firebaseAuthService = firebaseAuthService;
    }

    @GetMapping("/profile/api/token")
    public FirebaseAuthService.UserToken getToken() {
        return firebaseAuthService.generateToken();
    }

}
