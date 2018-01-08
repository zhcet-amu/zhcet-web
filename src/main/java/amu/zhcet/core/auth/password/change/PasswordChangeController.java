package amu.zhcet.core.auth.password.change;

import amu.zhcet.data.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Arrays;

@Slf4j
@Controller
public class PasswordChangeController {

    private final UserService userService;
    private final PasswordChangeService passwordChangeService;

    @Autowired
    public PasswordChangeController(UserService userService, PasswordChangeService passwordChangeService) {
        this.userService = userService;
        this.passwordChangeService = passwordChangeService;
    }

    @GetMapping("/profile/settings/password")
    public String changePassword(Model model) {
        String renderUrl = "user/change_password";
        userService.getLoggedInUser().ifPresent(user -> {
            if (!user.isEmailVerified()) {
                log.warn("User not verified and tried to change the password!");
                model.addAttribute("error", "The user is not verified, and hence can't change the password");
            } else {
                PasswordChange passwordChange = new PasswordChange();
                model.addAttribute("password", passwordChange);
                model.addAttribute("blacklist", Arrays.asList(
                        user.getName(),
                        user.getEmail(),
                        user.getUserId()
                ));
            }
        });

        return renderUrl;
    }

    @PostMapping("/profile/settings/password/change")
    public String savePassword(@Valid PasswordChange passwordChange, RedirectAttributes redirectAttributes) {
        String redirectUrl = "redirect:/profile/settings/password";

        return userService.getLoggedInUser()
                .map(user -> {

                    if (!user.isEmailVerified()) {
                        log.warn("!!POST!! User not verified and tried to change the password!");
                        redirectAttributes.addFlashAttribute("error", "The user is not verified, and hence can't change the password");
                    } else {
                        try {
                            passwordChangeService.changePassword(passwordChange, user);
                            redirectAttributes.addFlashAttribute("password_change_success", "Password was changed successfully");
                            return "redirect:/profile";
                        } catch (PasswordVerificationException pve) {
                            log.info("Password Change Error", pve);
                            redirectAttributes.addFlashAttribute("pass_errors", pve.getErrors());
                        }
                    }

                    return redirectUrl;
                }).orElse(redirectUrl);
    }

}
