package amu.zhcet.core.profile.avatar;

import amu.zhcet.core.auth.UserDetailService;
import amu.zhcet.data.user.User;
import amu.zhcet.storage.image.ImageService;
import amu.zhcet.storage.image.ImageUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
class AvatarService {

    public static final int AVATAR_TIMEOUT_HOURS = 24; // 1 day
    private static final int ORIGINAL_AVATAR_SIZE = 1000;
    private static final int AVATAR_SIZE = 86;

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

            long timeElapsed = getElapsedTime(user);
            if (timeElapsed < AVATAR_TIMEOUT_HOURS) {
                log.warn("Trying to update avatar before cool down : {} hours elapsed and {}  hours remaining",
                        timeElapsed, AVATAR_TIMEOUT_HOURS - timeElapsed);
                return false;
            }
        }

        return true;
    }

    public long getElapsedTime(User user) {
        ZonedDateTime lastUpdated = user.getDetails().getAvatarUpdated();
        return ChronoUnit.HOURS.between(lastUpdated, ZonedDateTime.now());
    }

    @Transactional
    public void uploadImage(User user, MultipartFile file) {
        log.info("Uploading photo " + file.getOriginalFilename() + " for " + user.getUserId());
        CompletableFuture<Optional<String>> avatarFuture = imageService.upload("profile/" + user.getUserId() + "/profile_thumb",
                file, AVATAR_SIZE);
        CompletableFuture<Optional<String>> originalAvatarFuture =imageService.upload("profile/" + user.getUserId() + "/profile",
                file, ORIGINAL_AVATAR_SIZE);

        CompletableFuture.allOf(avatarFuture, originalAvatarFuture).join();

        try {
            avatarFuture.get().ifPresent(user.getDetails()::setAvatarUrl);
            originalAvatarFuture.get().ifPresent(user.getDetails()::setOriginalAvatarUrl);
            user.getDetails().setAvatarUpdated(ZonedDateTime.now());
            userDetailService.saveAndUpdatePrincipal(user);
        } catch (InterruptedException | ExecutionException e) {
            throw new ImageUploadException(e.getMessage());
        }
    }

}
