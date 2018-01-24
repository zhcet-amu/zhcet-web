package amu.zhcet.core.profile.avatar;

import amu.zhcet.auth.AuthManager;
import amu.zhcet.auth.UserAuth;
import amu.zhcet.core.error.ErrorUtils;
import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserService;
import amu.zhcet.storage.image.ImageUtils;
import amu.zhcet.storage.image.upload.Avatar;
import amu.zhcet.storage.image.upload.ImageUploadException;
import amu.zhcet.storage.image.upload.ImageUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
class AvatarService {

    private static final int AVATAR_TIMEOUT_HOURS = 24; // 1 day

    private final ImageUploadService imageUploadService;
    private final UserService userService;

    @Autowired
    public AvatarService(ImageUploadService imageUploadService, UserService userService) {
        this.imageUploadService = imageUploadService;
        this.userService = userService;
    }

    private static void checkIfUploadingAllowed(User user) {
        ZonedDateTime lastUpdated = user.getDetails().getAvatarUpdated();
        if (lastUpdated != null) {
            log.info("User {} last updated its avatar at {}", user.getUserId(), lastUpdated.toString());

            long timeElapsed = getElapsedTime(user);
            if (timeElapsed < AVATAR_TIMEOUT_HOURS) {
                long timeRemaining = AVATAR_TIMEOUT_HOURS - timeElapsed;
                log.warn("Trying to update avatar before cool down : {} hours elapsed and {}  hours remaining", timeElapsed, timeRemaining);
                String message = String.format("You can only update profile picture once in %d hours. %d hours remaining for cool down", AVATAR_TIMEOUT_HOURS, timeRemaining);
                throw new IllegalStateException(message);
            }
        }
    }

    private static long getElapsedTime(User user) {
        ZonedDateTime lastUpdated = user.getDetails().getAvatarUpdated();
        return ChronoUnit.HOURS.between(lastUpdated, ZonedDateTime.now());
    }

    @Transactional
    public void uploadImage(UserAuth userAuth, MultipartFile file) {
        ErrorUtils.requireNonNullUser(userAuth);
        User user = userService.findByIdOrThrow(userAuth.getUsername());
        checkIfUploadingAllowed(user);

        log.info("Uploading photo " + file.getOriginalFilename() + " for " + userAuth.getUsername());
        ImageUtils.requireValidImage(file);

        try {
            Avatar avatar = imageUploadService.upload(userAuth, "profile/" + userAuth.getUsername() + "/avatar", file);

            user.getDetails().setOriginalAvatarUrl(avatar.getAvatar());
            user.getDetails().setAvatarUrl(avatar.getThumbnail());
            user.getDetails().setAvatarUpdated(ZonedDateTime.now());
            AuthManager.updateAvatar(userAuth, user.getDetails().getAvatarUrl());
            userService.save(user);
        } catch (InterruptedException | ExecutionException e) {
            throw new ImageUploadException(e.getMessage());
        }
    }

}
