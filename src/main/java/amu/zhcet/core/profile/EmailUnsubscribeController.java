package amu.zhcet.core.profile;

import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserNotFoundException;
import amu.zhcet.data.user.UserService;
import amu.zhcet.security.CryptoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class EmailUnsubscribeController {

    private final UserService userService;

    @Autowired
    public EmailUnsubscribeController(UserService userService) {
        this.userService = userService;
    }

    @ResponseBody
    @GetMapping("/login/unsubscribe")
    public String unsubscribe(@RequestParam String email, @RequestParam String conf) {
        User user = userService.getUserByEmail(email).orElseThrow(UserNotFoundException::new);
        if (!CryptoUtils.hashMatches(email, conf))
            return "Invalid Entry";
        userService.unsubscribeEmail(user, true);
        return "Unsubscribed";
    }

    @GetMapping("/profile/email/unsubscribe")
    public String unsubscribeEmail(@RequestParam(required = false) Boolean unsubscribe) {
        User user = userService.getLoggedInUser().orElseThrow(() -> new AccessDeniedException("403"));
        userService.unsubscribeEmail(user, unsubscribe != null && unsubscribe);
        return "redirect:/profile/settings#account";
    }

}
