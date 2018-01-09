package amu.zhcet.firebase.auth.verify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenVerifyController {

    private final TokenVerifyService tokenVerifyService;

    @Autowired
    public TokenVerifyController(TokenVerifyService tokenVerifyService) {
        this.tokenVerifyService = tokenVerifyService;
    }

    @PostMapping("/login/api/token")
    public String verifyToken(@RequestBody String token) {
        return tokenVerifyService.getAction(token);
    }

}
