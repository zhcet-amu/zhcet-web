package amu.zhcet.auth.twofactor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/profile/2fa")
public class TwoFAController {

    private final TwoFAService twoFAService;

    public TwoFAController(TwoFAService twoFAService) {
        this.twoFAService = twoFAService;
    }

    @GetMapping("/enable")
    public String enableGet(Model model) {
        try {
            model.addAttribute("qr_url", twoFAService.generate2FASecret());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "user/2fa_enable";
    }

    @PostMapping("/enable")
    public String enablePost(@RequestParam String code) {
        twoFAService.enable2FA(code);
        return "redirect:/profile/settings#security";
    }

}
