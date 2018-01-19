package amu.zhcet.core.profile.avatar;

import amu.zhcet.core.auth.UserDetailService;
import amu.zhcet.data.user.User;
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

    public static final int AVATAR_TIMEOUT_HOURS = 24; // 1 day

    private final ImageUploadService imageUploadService;
    private final UserDetailService userDetailService;

    @Autowired
    public AvatarService(ImageUploadService imageUploadService, UserDetailService userDetailService) {
        this.imageUploadService = imageUploadService;
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
        ImageUtils.requireValidImage(file);

        try {
            Avatar avatar = imageUploadService.upload(user, "profile/" + user.getUserId() + "/avatar", file);

            user.getDetails().setOriginalAvatarUrl(avatar.getAvatar());
            user.getDetails().setAvatarUrl(avatar.getThumbnail());
            user.getDetails().setAvatarUpdated(ZonedDateTime.now());
            userDetailService.saveAndUpdatePrincipal(user);
        } catch (InterruptedException | ExecutionException e) {
            throw new ImageUploadException(e.getMessage());
        }
    }

}
