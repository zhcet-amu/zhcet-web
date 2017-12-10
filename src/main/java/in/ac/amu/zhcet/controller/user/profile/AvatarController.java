package in.ac.amu.zhcet.controller.user.profile;

import in.ac.amu.zhcet.service.upload.image.ImageService;
import in.ac.amu.zhcet.service.upload.image.ImageUploadException;
import in.ac.amu.zhcet.service.user.UserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

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
        String redirectUrl = "redirect:/profile";

       userDetailService.getLoggedInUser().ifPresent(user -> {
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
                   return;
               }
           }

           try {
               log.warn("Uploading photo " + file.getOriginalFilename() + " for " + user.getUserId());
               userDetailService.updateAvatar(user, imageService.uploadAvatar("profile/" + user.getUserId() + "/profile", file));
               redirectAttributes.addFlashAttribute("avatar_success", Collections.singletonList("Profile Picture Updated"));
           } catch (ImageUploadException ex) {
               redirectAttributes.addFlashAttribute("avatar_errors", Collections.singletonList(ex.getMessage()));
           }
       });

        return redirectUrl;
    }

}
