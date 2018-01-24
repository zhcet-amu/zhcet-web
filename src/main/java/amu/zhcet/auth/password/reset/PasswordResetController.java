package amu.zhcet.auth.password.reset;

import amu.zhcet.auth.Auditor;
import amu.zhcet.auth.password.PasswordReset;
import amu.zhcet.auth.password.PasswordValidationException;
import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/login/password/reset")
class PasswordResetController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    public PasswordResetController(UserService userService, PasswordResetService passwordResetService) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
    }

    @GetMapping
    public String resetPassword(Model model, @RequestParam String hash, @RequestParam("auth") String token) {
        try {
            passwordResetService.grantAccess(hash, token);
            if (!model.containsAttribute("password")) {
                PasswordReset passwordReset = new PasswordReset();
                passwordReset.setHash(hash);
                passwordReset.setToken(token);
                model.addAttribute("password", passwordReset);
                model.addAttribute("blacklist", Collections.EMPTY_LIST);
            }
        } catch (TokenValidationException tve) {
            log.warn("Token Verification : Password Reset : {}", tve);
            model.addAttribute("error", tve.getMessage());
        }

        return "user/reset_password";
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PASSWORD_CHANGE_PRIVILEGE')")
    public String savePassword(@Valid PasswordReset passwordReset, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = Auditor.getLoggedInAuthentication()
                .map(Authentication::getPrincipal)
                .filter(principal -> !principal.getClass().isAssignableFrom(User.class))
                .map(principal -> ((User) principal).getUserId())
                .flatMap(userService::findById);

        if (!optionalUser.isPresent()) {
            redirectAttributes.addAttribute("error", "Unknown Error");
        } else {
            User user = optionalUser.get();
            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("password", passwordReset);
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.password", bindingResult);
            } else {
                try {
                    passwordResetService.resetPassword(user, passwordReset);
                    redirectAttributes.addFlashAttribute("reset_success", true);
                    return "redirect:/login";
                } catch (TokenValidationException tve) {
                    log.warn("Token Verification : Password Reset : {}", tve.getMessage());
                    redirectAttributes.addAttribute("error", tve.getMessage());
                } catch (PasswordValidationException pve) {
                    log.info("Password Verification Exception", pve);
                    redirectAttributes.addFlashAttribute("pass_errors", pve.getMessage());
                }
            }
        }

        return String.format("redirect:/login/password/reset?hash=%s&auth=%s", passwordReset.getHash(), passwordReset.getToken());
    }

}
