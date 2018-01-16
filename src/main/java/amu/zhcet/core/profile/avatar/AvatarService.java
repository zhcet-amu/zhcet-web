package amu.zhcet.core.profile.avatar;

import amu.zhcet.core.auth.UserDetailService;
import amu.zhcet.data.user.User;
import amu.zhcet.storage.image.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
class AvatarService {

    private static final int AVATAR_TIMEOUT_HOURS = 24; // 1 day

    private final ImageService imageService;
    private final UserDetailService userDetailService;

    @Autowired
    public AvatarService(ImageService imageService, UserDetailService userDetailService) {
        this.imageService = imageService;
        this.userDetailService = userDetailService;
    }

    public boolean isUploadingAllowed(User user) {
        ZonedDateTime lastUpdated = user.getDetails().getAvatarUpdated();
        if (lastUpdated != null) {
            log.info("User {} last updated its avatar at {}", user.getUserId(), lastUpdated.toString());

            long timeElapsed = ChronoUnit.HOURS.between(lastUpdated, ZonedDateTime.now());
            if (timeElapsed < AVATAR_TIMEOUT_HOURS) {
                log.warn("Trying to update avatar before cool down : {} hours elapsed and {}  hours remaining", timeElapsed, AVATAR_TIMEOUT_HOURS - timeElapsed);

                return false;
            }
        }

        return true;
    }

    public void uploadImage(User user, MultipartFile file) {
        log.info("Uploading photo " + file.getOriginalFilename() + " for " + user.getUserId());
        try {
            ImageService.Avatar avatar = imageService.uploadAvatar("profile/" + user.getUserId() + "/profile", file);
            userDetailService.updateAvatar(user, avatar.getOriginalAvatarUrl(), avatar.getAvatarUrl());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error uploading avatar", e);
            throw new RuntimeException("Error Uploading Avatar");
        }
    }

}
