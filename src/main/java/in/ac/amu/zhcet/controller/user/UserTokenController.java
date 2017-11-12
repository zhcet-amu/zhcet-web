package in.ac.amu.zhcet.controller.user;

import in.ac.amu.zhcet.service.firebase.FirebaseAuthService;
import in.ac.amu.zhcet.service.firebase.UserToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public UserToken getToken() {
        return firebaseAuthService.generateToken();
    }

    @PostMapping("/login/api/token")
    public String postToken(@RequestBody String token) {
        return firebaseAuthService.getAction(token);
    }

}
