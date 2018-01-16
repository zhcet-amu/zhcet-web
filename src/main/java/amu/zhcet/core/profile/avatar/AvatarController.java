package amu.zhcet.core.profile.avatar;

import amu.zhcet.common.flash.Flash;
import amu.zhcet.core.auth.UserDetailService;
import amu.zhcet.data.user.User;
import amu.zhcet.storage.image.ImageUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

@Slf4j
@Controller
public class AvatarController {

    private final UserDetailService userDetailService;
    private final AvatarService avatarService;

    @Autowired
    public AvatarController(UserDetailService userDetailService, AvatarService avatarService) {
        this.userDetailService = userDetailService;
        this.avatarService = avatarService;
    }

    @PostMapping("/profile/picture")
    public String handleFileUpload(@RequestParam MultipartFile file, RedirectAttributes redirectAttributes) {
        User user = userDetailService.getUserService().getLoggedInUser().orElseThrow(() -> new AccessDeniedException("403"));

        if (avatarService.isUploadingAllowed(user)) {
            try {
                avatarService.uploadImage(user, file);
                redirectAttributes.addFlashAttribute("avatar_success", Collections.singletonList("Profile Picture Updated"));
            } catch (ImageUploadException ime) {
                redirectAttributes.addFlashAttribute("avatar_errors", Collections.singletonList(ime.getMessage()));
            } catch (RuntimeException re) {
                redirectAttributes.addFlashAttribute("flash_messages", Flash.error("Failed to upload avatar"));
            }
        }

        return "redirect:/profile";
    }



}
