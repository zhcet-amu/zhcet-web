package amu.zhcet.auth.password.reset;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/login/password/forgot")
class ForgotPasswordController {

    private final ResetTokenSender resetTokenSender;

    @Autowired
    public ForgotPasswordController(ResetTokenSender resetTokenSender) {
        this.resetTokenSender = resetTokenSender;
    }

    @GetMapping
    public String getForgetPassword() {
        return "user/forgot_password";
    }

    @PostMapping
    public String sendEmailLink(RedirectAttributes redirectAttributes, @RequestParam String email) {
        try {
            resetTokenSender.sendResetToken(email);
            redirectAttributes.addFlashAttribute("reset_link_sent", true);
        } catch(UsernameNotFoundException e){
            log.warn("User not found : Password Forgot : {}", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login/password/forgot";
        }

        return "redirect:/login";
    }

}
