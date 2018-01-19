package amu.zhcet.core.profile.avatar;

import amu.zhcet.common.flash.Flash;
import amu.zhcet.core.auth.UserDetailService;
import amu.zhcet.data.user.User;
import amu.zhcet.storage.image.edit.ImageEditException;
import amu.zhcet.storage.image.upload.ImageUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

import static amu.zhcet.core.profile.avatar.AvatarService.AVATAR_TIMEOUT_HOURS;

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
            } catch (ImageUploadException| ImageEditException ime) {
                redirectAttributes.addFlashAttribute("avatar_errors", Collections.singletonList(ime.getMessage()));
            } catch (RuntimeException re) {
                redirectAttributes.addFlashAttribute("flash_messages", Flash.error("Failed to upload avatar"));
            }
        } else {
            long timeElapsed = avatarService.getElapsedTime(user);
            redirectAttributes.addFlashAttribute("avatar_errors", Collections.singletonList(String.format(
                            "You can only update profile picture once in %d hours. %d hours remaining for cool down",
                                    AVATAR_TIMEOUT_HOURS, AVATAR_TIMEOUT_HOURS - timeElapsed)));
        }

        return "redirect:/profile";
    }



}
