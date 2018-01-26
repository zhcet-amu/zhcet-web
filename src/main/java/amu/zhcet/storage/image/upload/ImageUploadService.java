package amu.zhcet.storage.image.upload;

import amu.zhcet.auth.UserAuth;
import amu.zhcet.storage.image.Image;
import amu.zhcet.storage.image.edit.ImageEditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class ImageUploadService {

    private static final int ORIGINAL_AVATAR_SIZE = 1000;
    private static final int AVATAR_SIZE = 86;

    private final FileImageUploadService fileImageUploadService;
    private final FirebaseImageAsyncHandler firebaseImageAsyncHandler;
    private final ImageEditService imageEditService;

    @Autowired
    public ImageUploadService(FileImageUploadService fileImageUploadService, FirebaseImageAsyncHandler firebaseImageAsyncHandler, ImageEditService imageEditService) {
        this.fileImageUploadService = fileImageUploadService;
        this.firebaseImageAsyncHandler = firebaseImageAsyncHandler;
        this.imageEditService = imageEditService;
    }

    public String upload(Image image) {
        return uploadImage(image).getUrl();
    }

    UploadedImage uploadImage(Image image) {
        return fileImageUploadService.uploadImage(image);
    }

    public Avatar upload(UserAuth user, String name, MultipartFile file) throws ExecutionException, InterruptedException {
        // Normalize (Crop) Image in parallel
        CompletableFuture<Image> avatarFuture = imageEditService.normalizeAsync(file, ORIGINAL_AVATAR_SIZE);
        CompletableFuture<Image> avatarThumbnailFuture = imageEditService.normalizeAsync(file, AVATAR_SIZE);

        CompletableFuture.allOf(avatarFuture, avatarThumbnailFuture).join();

        // Prepare Images to be saved
        Image avatarImage = avatarFuture.get();
        Image avatarThumbnail = avatarThumbnailFuture.get();

        avatarImage.setName(name);
        avatarThumbnail.setName(name + "_thumb");
        avatarThumbnail.setThumbnail(true);

        // Generate Avatar and send images for upload
        Avatar avatar = new Avatar();
        UploadedImage uploadedAvatar = uploadImage(avatarImage);
        avatar.setAvatar(uploadedAvatar.getUrl());
        UploadedImage uploadedThumbnail = uploadImage(avatarImage);
        uploadedThumbnail.setThumbnail(true);
        avatar.setThumbnail(uploadedThumbnail.getUrl());

        uploadToCloud(user, avatarImage, uploadedAvatar);
        uploadToCloud(user, avatarThumbnail, uploadedThumbnail);

        return avatar;

    }

    private void uploadToCloud(UserAuth user, Image avatarImage, UploadedImage uploadedAvatar) {
        // Save user information in database
        log.info("Saving user info with uploaded image");
        uploadedAvatar.setUser(user.getUsername());
        fileImageUploadService.save(uploadedAvatar);
        // Send to cloud
        log.info("Sending to firebase...");
        firebaseImageAsyncHandler.handleUploadedImage(user, avatarImage, uploadedAvatar);
        log.info("Sent");
    }

}
