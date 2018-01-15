package amu.zhcet.core.profile.avatar;

import amu.zhcet.common.flash.Flash;
import amu.zhcet.core.auth.UserDetailService;
import amu.zhcet.data.user.User;
import amu.zhcet.storage.image.ImageService;
import amu.zhcet.storage.image.ImageUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

@Slf4j
@Controller
public class AvatarController {

    private static final int AVATAR_TIMEOUT_HOURS = 24; // 1 day

    private final UserDetailService userDetailService;
    private final ImageService imageService;

    @Autowired
    public AvatarController(UserDetailService userDetailService, ImageService imageService) {
        this.userDetailService = userDetailService;
        this.imageService = imageService;
    }

    @PostMapping("/profile/picture")
    public String handleFileUpload(@RequestParam MultipartFile file, RedirectAttributes redirectAttributes) {
        // TODO: Break into AvatarService
        User user = userDetailService.getUserService().getLoggedInUser().orElseThrow(() -> new AccessDeniedException("403"));

        ZonedDateTime lastUpdated = user.getDetails().getAvatarUpdated();
        if (lastUpdated != null) {
            log.info("User {} last updated its avatar at {}", user.getUserId(), lastUpdated.toString());

            long timeElapsed = ChronoUnit.HOURS.between(lastUpdated, ZonedDateTime.now());
            if (timeElapsed < AVATAR_TIMEOUT_HOURS) {
                log.warn("Trying to update avatar before cool down : {} hours elapsed and {}  hours remaining", timeElapsed, AVATAR_TIMEOUT_HOURS - timeElapsed);

                redirectAttributes.addFlashAttribute("avatar_errors",
                        Collections.singletonList(
                                String.format("You can only update profile picture once in %d hours. %d hours remaining for cooldown",
                                        AVATAR_TIMEOUT_HOURS, AVATAR_TIMEOUT_HOURS - timeElapsed)
                        )
                );
            } else {
                uploadImage(user, file, redirectAttributes);
            }
        }

        uploadImage(user, file, redirectAttributes);

        return "redirect:/profile";
    }

    private void uploadImage(User user, MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            log.warn("Uploading photo " + file.getOriginalFilename() + " for " + user.getUserId());
            try {
                ImageService.Avatar avatar = imageService.uploadAvatar("profile/" + user.getUserId() + "/profile", file);
                userDetailService.updateAvatar(user, avatar.getOriginalAvatarUrl(), avatar.getAvatarUrl());
            } catch (InterruptedException | ExecutionException e) {
                redirectAttributes.addFlashAttribute("flash_messages", Flash.error("Failed to upload avatar"));
            }
            redirectAttributes.addFlashAttribute("avatar_success", Collections.singletonList("Profile Picture Updated"));
        } catch (ImageUploadException ex) {
            redirectAttributes.addFlashAttribute("avatar_errors", Collections.singletonList(ex.getMessage()));
        }
    }

}
