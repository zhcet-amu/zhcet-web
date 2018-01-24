package amu.zhcet.auth.password.change;

import amu.zhcet.auth.password.PasswordValidationException;
import amu.zhcet.auth.password.PasswordChange;
import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserNotFoundException;
import amu.zhcet.data.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Arrays;

@Slf4j
@Controller
@RequestMapping("/profile/password/change")
class PasswordChangeController {

    private final UserService userService;
    private final PasswordChangeService passwordChangeService;

    @Autowired
    public PasswordChangeController(UserService userService, PasswordChangeService passwordChangeService) {
        this.userService = userService;
        this.passwordChangeService = passwordChangeService;
    }

    @GetMapping
    public String changePassword(Model model) {
        User user = userService.getLoggedInUser().orElseThrow(UserNotFoundException::new);

        if (!user.isEmailVerified()) {
            log.warn("User not verified and tried to change the password!");
            model.addAttribute("error", "The user is not verified, and hence can't change the password");
        } else {
            if (!model.containsAttribute("password")) {
                PasswordChange passwordChange = new PasswordChange();
                model.addAttribute("password", passwordChange);
            }
            model.addAttribute("blacklist", Arrays.asList(
                    user.getName(),
                    user.getEmail(),
                    user.getUserId()
            ));
        }

        return "user/change_password";
    }

    @PostMapping
    public String savePassword(@Valid PasswordChange passwordChange, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        User user = userService.getLoggedInUser().orElseThrow(UserNotFoundException::new);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("password", passwordChange);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.password", bindingResult);
        } else  {
            try {
                passwordChangeService.changePassword(user, passwordChange);
                redirectAttributes.addFlashAttribute("password_change_success", "Password was changed successfully");
                return "redirect:/profile";
            } catch (PasswordValidationException pve) {
                redirectAttributes.addFlashAttribute("pass_errors", pve.getMessage());
            }
        }

        return "redirect:/profile/password/change";
    }

}
