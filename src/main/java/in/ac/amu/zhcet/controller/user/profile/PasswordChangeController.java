package in.ac.amu.zhcet.controller.user.profile;

import in.ac.amu.zhcet.data.model.dto.PasswordChange;
import in.ac.amu.zhcet.data.model.user.User;
import in.ac.amu.zhcet.service.UserService;
import in.ac.amu.zhcet.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
public class PasswordChangeController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordChangeController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/profile/settings/password")
    public String changePassword(Model model) {
        String renderUrl = "user/change_password";
        User user = userService.getLoggedInUser();
        if (!user.isEmailVerified()) {
            log.warn("User not verified and tried to change the password!");
            model.addAttribute("error", "The user is not verified, and hence can't change the password");
            return renderUrl;
        }
        PasswordChange passwordChange = new PasswordChange();
        model.addAttribute("password", passwordChange);
        model.addAttribute("blacklist", Arrays.asList(
                user.getName(),
                user.getEmail(),
                user.getUserId()
        ));
        return renderUrl;
    }

    @PostMapping("/profile/settings/password/change")
    public String savePassword(@Valid PasswordChange passwordChange, RedirectAttributes redirectAttributes) {
        String redirectUrl = "redirect:/profile/settings/password";

        User user = userService.getLoggedInUser();
        if (!user.isEmailVerified()) {
            log.warn("!!POST!! User not verified and tried to change the password!");
            redirectAttributes.addFlashAttribute("error", "The user is not verified, and hence can't change the password");
            return redirectUrl;
        }

        if (!passwordEncoder.matches(passwordChange.getOldPassword(), user.getPassword())) {
            log.warn("Current password does not match");
            redirectAttributes.addFlashAttribute("pass_errors", "Current password does not match provided password");
            return redirectUrl;
        }

        List<String> errors = SecurityUtils.validatePassword(passwordChange.getNewPassword(), passwordChange.getConfirmPassword());

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("pass_errors", errors);
            return redirectUrl;
        }

        if (passwordChange.getOldPassword().equals(passwordChange.getNewPassword())) {
            redirectAttributes.addFlashAttribute("pass_errors", Collections.singletonList("New and old password cannot be same"));
            return redirectUrl;
        }

        userService.changeUserPassword(user, passwordChange.getNewPassword());
        redirectAttributes.addFlashAttribute("password_change_success", "Password was changed successfully");
        return "redirect:/profile";
    }

}
