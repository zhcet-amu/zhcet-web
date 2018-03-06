package amu.zhcet.core.profile.avatar;

import amu.zhcet.common.flash.Flash;
import amu.zhcet.auth.UserAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

@Slf4j
@Controller
@RequestMapping("/profile/picture")
@PreAuthorize("@authService.isFullyAuthenticated(principal)")
public class AvatarController {

    private final AvatarService avatarService;

    @Autowired
    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping
    public String handleFileUpload(@AuthenticationPrincipal UserAuth userAuth, @RequestParam MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            avatarService.uploadImage(userAuth, file);
            redirectAttributes.addFlashAttribute("avatar_success", Collections.singletonList("Profile Picture Updated"));
        } catch (IllegalStateException ise) {
            redirectAttributes.addFlashAttribute("avatar_errors", Collections.singletonList(ise.getMessage()));
        } catch (RuntimeException re) {
            redirectAttributes.addFlashAttribute("flash_messages", Flash.error("Failed to upload avatar"));
        }

        return "redirect:/profile/settings";
    }

}
