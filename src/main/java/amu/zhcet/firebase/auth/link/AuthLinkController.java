package amu.zhcet.firebase.auth.link;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthLinkController {

    private final AuthLinkService authLinkService;

    @Autowired
    public AuthLinkController(AuthLinkService authLinkService) {
        this.authLinkService = authLinkService;
    }

    @PostMapping("/profile/api/link")
    public String linkAccount(@RequestBody String token) {
        authLinkService.linkAccount(token);
        return "OK";
    }

}
