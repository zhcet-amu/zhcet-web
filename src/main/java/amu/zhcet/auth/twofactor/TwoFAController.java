package amu.zhcet.auth.twofactor;

import amu.zhcet.common.flash.Flash;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile/2fa")
@PreAuthorize("@authService.isFullyAuthenticated(principal)")
public class TwoFAController {

    private static final String TWO_FACTOR_ENABLED_MESSAGE = "Enabled 2 Factor Authentication";

    private final TwoFAService twoFAService;

    public TwoFAController(TwoFAService twoFAService) {
        this.twoFAService = twoFAService;
    }

    @RequestMapping("/enable")
    public String enableGet(Model model, RedirectAttributes redirectAttributes, @RequestParam(required = false) Boolean retain) {
        if (retain != null && retain) {
            twoFAService.enable2FA();
            redirectAttributes.addFlashAttribute("flash_messages", Flash.success(TWO_FACTOR_ENABLED_MESSAGE));
            return "redirect:/profile/settings#security";
        }

        TwoFAService.TwoFASecret secret = twoFAService.generate2FASecret();
        model.addAttribute("secret", secret);

        return "user/2fa_enable";
    }

    @PostMapping("/confirm")
    public String enablePost(RedirectAttributes redirectAttributes, @RequestParam String secret, @RequestParam String code) {
        try {
            twoFAService.enable2FA(secret, code);
            redirectAttributes.addFlashAttribute("flash_messages", Flash.success(TWO_FACTOR_ENABLED_MESSAGE));
        } catch (RuntimeException re) {
            redirectAttributes.addFlashAttribute("flash_messages", Flash.error(re.getMessage()));
            return "redirect:/profile/2fa/enable";
        }
        return "redirect:/profile/settings#security";
    }

    @PostMapping("/disable")
    public String disable(RedirectAttributes redirectAttributes) {
        twoFAService.disable2FA();
        redirectAttributes.addFlashAttribute("flash_messages", Flash.success("Disabled 2 Factor Authentication"));
        return "redirect:/profile/settings#security";
    }

}
