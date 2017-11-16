package in.ac.amu.zhcet.controller.user;

import in.ac.amu.zhcet.service.firebase.auth.FirebaseAuthService;
import in.ac.amu.zhcet.service.firebase.auth.UserToken;
import in.ac.amu.zhcet.service.firebase.messaging.FirebaseMessagingService;
import in.ac.amu.zhcet.service.user.Auditor;
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
    private final FirebaseMessagingService firebaseMessagingService;

    @Autowired
    public UserTokenController(FirebaseAuthService firebaseAuthService, FirebaseMessagingService firebaseMessagingService) {
        this.firebaseAuthService = firebaseAuthService;
        this.firebaseMessagingService = firebaseMessagingService;
    }

    @GetMapping("/profile/api/token")
    public UserToken getToken() {
        return firebaseAuthService.generateToken();
    }

    @PostMapping("/login/api/token")
    public String postToken(@RequestBody String token) {
        return firebaseAuthService.getAction(token);
    }

    @PostMapping("/profile/api/link")
    public String linkData(@RequestBody String token) {
        firebaseAuthService.linkData(token);
        return "OK";
    }

    @PostMapping("/profile/api/messaging_token")
    public String postMessagingToken(@RequestBody String token) {
        firebaseMessagingService.attachToken(Auditor.getLoggedInUsername(), token);
        return "OK";
    }

}
