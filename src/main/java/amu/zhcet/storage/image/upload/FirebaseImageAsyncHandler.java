package amu.zhcet.storage.image.upload;

import amu.zhcet.auth.AuthManager;
import amu.zhcet.auth.UserAuth;
import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserService;
import amu.zhcet.storage.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class FirebaseImageAsyncHandler {

    private final UserService userService;
    private final FileImageUploadService fileImageUploadService;
    private final FirebaseImageUploadService firebaseImageUploadService;

    @Autowired
    public FirebaseImageAsyncHandler(FileImageUploadService fileImageUploadService,
                                     UserService userService,
                                     FirebaseImageUploadService firebaseImageUploadService) {
        this.fileImageUploadService = fileImageUploadService;
        this.userService = userService;
        this.firebaseImageUploadService = firebaseImageUploadService;
    }

    @Async
    public void handleUploadedImage(UserAuth user, Image image, UploadedImage uploadedImage) {
        Optional<String> avatarUploadFuture = firebaseImageUploadService.upload(image).join();

        if (!avatarUploadFuture.isPresent()) {
            log.warn("Could not upload image to firebase...");
            return;
        }

        log.info("File uploaded to Firebase : {} {} {}", user.getUsername(), image, uploadedImage);

        String url = avatarUploadFuture.get();
        Optional<User> stored = userService.findById(user.getUsername());

        if (!stored.isPresent()) {
            log.warn("Tried to update avatar of non-existent user {} {}", image, uploadedImage);
            return;
        }

        log.info("Saving firebase URL to avatar for user : {}", user.getUsername());

        User storedUser = stored.get();
        if (uploadedImage.isThumbnail()) {
            log.info("Saving link as thumbnail");
            storedUser.getDetails().setAvatarUrl(url);
        } else {
            log.info("Saving link as avatar");
            storedUser.getDetails().setOriginalAvatarUrl(url);
        }

        log.info("Saving user...");
        AuthManager.updateAvatar(user, storedUser.getDetails().getAvatarUrl());
        userService.save(storedUser);

        log.info("Deleting File {}", uploadedImage);
        fileImageUploadService.delete(uploadedImage);
    }

}
