package in.ac.amu.zhcet.controller.profile;

import in.ac.amu.zhcet.data.model.token.VerificationToken;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.service.UserService;
import in.ac.amu.zhcet.service.user.auth.DuplicateEmailException;
import in.ac.amu.zhcet.service.user.auth.EmailVerificationService;
import in.ac.amu.zhcet.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

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
            VerificationToken token = emailVerificationService.generate(email);
            emailVerificationService.sendMail(token);
            redirectAttributes.addFlashAttribute("link_sent", "Verification link sent to '" + email + "'!");
        } catch (DuplicateEmailException de) {
            log.warn("Duplicate Email", de);
            redirectAttributes.addFlashAttribute("duplicate_email", de.getMessage());
        }
    }

    @PostMapping("/profile/register_email")
    public String registerEmail(RedirectAttributes redirectAttributes, @RequestParam String email) {
        if (Utils.isValidEmail(email)) {
            sendVerificationLink(email, redirectAttributes);
        } else {
            log.warn("Invalid Email", email);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("invalid_email", "The provided email is invalid!");
        }

        return "redirect:/profile";
    }

    @PostMapping("/profile/confirm_email")
    public String registerEmail(RedirectAttributes redirectAttributes, HttpServletRequest request) {
        UserAuth user = userService.getLoggedInUser();
        String email = user.getEmail();

        if (Utils.isValidEmail(user.getEmail())) {
            sendVerificationLink(email, redirectAttributes);
        } else {
            log.warn("Invalid Email", email);
            redirectAttributes.addFlashAttribute("invalid_email", "The provided email is invalid!");
        }

        return "redirect:/profile";
    }

    @GetMapping("/login/verify")
    public String resetPassword(Model model, @RequestParam("auth") String token){
        String result = emailVerificationService.validate(token);
        if (result != null) {
            log.warn("Email Verification Error", result);
            model.addAttribute("error", result);
        } else {
            emailVerificationService.confirmEmail(token);
            model.addAttribute("success", "Your email was successfully verified!");
        }
        return "user/verify_email";
    }

}
