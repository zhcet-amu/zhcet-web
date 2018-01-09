package amu.zhcet.firebase.auth.grant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenGrantController {

    private final TokenGrantService tokenGrantService;

    public TokenGrantController(TokenGrantService tokenGrantService) {
        this.tokenGrantService = tokenGrantService;
    }

    @GetMapping("/profile/api/token")
    public UserToken getToken() {
        return tokenGrantService.generateToken();
    }

}
