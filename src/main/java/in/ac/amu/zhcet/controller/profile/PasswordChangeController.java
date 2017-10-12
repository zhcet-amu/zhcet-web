package in.ac.amu.zhcet.controller.profile;

import in.ac.amu.zhcet.data.model.dto.PasswordChange;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.service.UserService;
import in.ac.amu.zhcet.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
public class PasswordChangeController {

    private final UserService userService;

    @Autowired
    public PasswordChangeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile/change_password")
    public String changePassword(Model model) {
        String renderUrl = "user/change_password";
        UserAuth userAuth = userService.getLoggedInUser();
        if (!userAuth.isActive()) {
            log.warn("User not verified and tried to change the password!");
            model.addAttribute("error", "The user is not verified, and hence can't change the password");
            return renderUrl;
        }
        PasswordChange passwordChange = new PasswordChange();
        model.addAttribute("password", passwordChange);
        return renderUrl;
    }

    @PostMapping("/profile/change_password")
    public String savePassword(@Valid PasswordChange passwordChange, RedirectAttributes redirectAttributes) {
        String redirectUrl = "redirect:/profile/change_password";

        UserAuth userAuth = userService.getLoggedInUser();
        if (!userAuth.isActive()) {
            log.warn("!!POST!! User not verified and tried to change the password!");
            redirectAttributes.addFlashAttribute("error", "The user is not verified, and hence can't change the password");
            return redirectUrl;
        }

        if (!UserAuth.PASSWORD_ENCODER.matches(passwordChange.getOldPassword(), userAuth.getPassword())) {
            log.warn("Current password does not match");
            redirectAttributes.addFlashAttribute("pass_errors", "Current password does not match provided password");
            return redirectUrl;
        }

        List<String> errors = Utils.validatePassword(passwordChange.getNewPassword(), passwordChange.getConfirmPassword());

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("pass_errors", errors);
            return redirectUrl;
        }

        if (passwordChange.getOldPassword().equals(passwordChange.getNewPassword())) {
            redirectAttributes.addFlashAttribute("pass_errors", Collections.singletonList("New and old password cannot be same"));
            return redirectUrl;
        }

        userService.changeUserPassword(userAuth, passwordChange.getNewPassword());
        redirectAttributes.addFlashAttribute("password_change_success", "Password was changed successfully");
        return "redirect:/profile";
    }

}
