package amu.zhcet.auth.verification;

import amu.zhcet.common.utils.Utils;
import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
public class EmailVerificationController {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    @Autowired
    public EmailVerificationController(UserService userService, EmailVerificationService emailVerificationService) {
        this.userService = userService;
        this.emailVerificationService = emailVerificationService;
    }

    private void sendVerificationLink(String email, RedirectAttributes redirectAttributes) {
        try {
            emailVerificationService.generate(email);
            redirectAttributes.addFlashAttribute("email_success", "Verification link sent to '" + email + "'!");
        } catch (DuplicateEmailException de) {
            log.warn("Duplicate Email", de);
            redirectAttributes.addFlashAttribute("email_error", de.getMessage());
        } catch (RecentVerificationException re) {
            log.warn("Recently Sent Email", re);
            redirectAttributes.addFlashAttribute("email_error", RecentVerificationException.MESSAGE);
        } catch (RuntimeException re) {
            log.warn("Error sending verification link", re);
            redirectAttributes.addFlashAttribute("email_error",
                    "There was some error sending the email");
        }
    }

    @PostMapping("/profile/email/register")
    public String registerEmail(RedirectAttributes redirectAttributes, @RequestParam String email) {
        User user = userService.getLoggedInUser().orElseThrow(() -> new AccessDeniedException("403"));

        if (user.getEmail() != null && user.getEmail().equals(email)) {
            redirectAttributes.addFlashAttribute("email_error", "New email is same as previous one");
        } else if (Utils.isValidEmail(email)) {
            sendVerificationLink(email, redirectAttributes);
        } else {
            log.warn("Invalid Email", email);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("email_error", "The provided email is invalid!");
        }

        return "redirect:/profile/settings#account";
    }

    @PostMapping("/profile/email/resend_link")
    public String resendLink(RedirectAttributes redirectAttributes) {
        User user = userService.getLoggedInUser().orElseThrow(() -> new AccessDeniedException("403"));

        String email = user.getEmail();

        if (Utils.isValidEmail(user.getEmail())) {
            sendVerificationLink(email, redirectAttributes);
        } else {
            log.warn("Invalid Email", email);
            redirectAttributes.addFlashAttribute("email_error", "The provided email is invalid!");
        }

        return "redirect:/profile/settings#account";
    }

    @GetMapping("/login/email/verify")
    public String verifyEmail(Model model, @RequestParam("auth") String token) {
        try {
            emailVerificationService.verifyEmail(token);
            model.addAttribute("success", "Your email was successfully verified!");
        } catch (DuplicateEmailException | TokenVerificationException de) {
            log.warn("Email Verification Error {}", de.getMessage());
            model.addAttribute("error", de.getMessage());
        }

        return "user/verify_email";
    }

}
