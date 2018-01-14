package amu.zhcet.core.auth.password.reset;

import amu.zhcet.core.auth.password.change.PasswordReset;
import amu.zhcet.core.auth.password.change.PasswordVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/login/password/reset")
public class ResetPasswordController {

    private final PasswordResetService passwordResetService;

    public ResetPasswordController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping
    public String resetPassword(Model model, @RequestParam String hash, @RequestParam("auth") String token){

        try {
            passwordResetService.validate(hash, token);
            PasswordReset passwordReset = new PasswordReset();
            passwordReset.setHash(hash);
            passwordReset.setToken(token);
            model.addAttribute("password", passwordReset);
        } catch (TokenValidationException tve) {
            log.warn("Token Verification : Password Reset : {}", tve);
            model.addAttribute("error", tve.getMessage());
        }

        return "user/reset_password";
    }

    @PostMapping
    public String savePassword(@Valid PasswordReset passwordReset, RedirectAttributes redirectAttributes) {
        String redirectUrl = String.format("redirect:/login/password/reset?hash=%s&auth=%s", passwordReset.getHash(), passwordReset.getToken());

        try {
            passwordResetService.resetPassword(passwordReset);
            redirectAttributes.addFlashAttribute("reset_success", true);
            return "redirect:/login";
        } catch (TokenValidationException tve) {
            log.warn("Token Verification : Password Reset : {}", tve.getMessage());
            redirectAttributes.addAttribute("error", tve.getMessage());
        } catch (PasswordVerificationException pve) {
            log.info("Password Verification Exception", pve);
            redirectAttributes.addFlashAttribute("pass_errors", pve.getErrors());
        }

        return redirectUrl;
    }

}
