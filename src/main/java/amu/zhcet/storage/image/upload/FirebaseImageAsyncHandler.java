package amu.zhcet.storage.image.upload;

import amu.zhcet.core.auth.UserDetailService;
import amu.zhcet.data.user.User;
import amu.zhcet.storage.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class FirebaseImageAsyncHandler {

    private final FileImageUploadService fileImageUploadService;
    private final UserDetailService userDetailService;
    private final FirebaseImageUploadService firebaseImageUploadService;

    @Autowired
    public FirebaseImageAsyncHandler(FileImageUploadService fileImageUploadService, UserDetailService userDetailService, FirebaseImageUploadService firebaseImageUploadService) {
        this.fileImageUploadService = fileImageUploadService;
        this.userDetailService = userDetailService;
        this.firebaseImageUploadService = firebaseImageUploadService;
    }

    @Async
    public void handleUploadedImage(User user, Image image, UploadedImage uploadedImage) {
        Optional<String> avatarUploadFuture = firebaseImageUploadService.upload(image).join();

        if (!avatarUploadFuture.isPresent()) {
            log.warn("Could not upload image to firebase...");
            return;
        }

        log.info("File uploaded to Firebase : {} {} {}", user.getUserId(), image, uploadedImage);

        String url = avatarUploadFuture.get();
        Optional<User> stored = userDetailService.getUserService().findById(user.getUserId());

        if (!stored.isPresent()) {
            log.warn("Tried to update avatar of non-existent user {} {}", image, uploadedImage);
            return;
        }

        log.info("Saving firebase URL to avatar for user : {}", user.getUserId());

        User storedUser = stored.get();
        if (uploadedImage.isThumbnail()) {
            log.info("Saving link as thumbnail");
            storedUser.getDetails().setAvatarUrl(url);
        } else {
            log.info("Saving link as avatar");
            storedUser.getDetails().setOriginalAvatarUrl(url);
        }

        log.info("Saving user...");
        userDetailService.saveAndUpdateFakePrincipal(storedUser, false);

        log.info("Deleting File {}", uploadedImage);
        fileImageUploadService.delete(uploadedImage);
    }

}
