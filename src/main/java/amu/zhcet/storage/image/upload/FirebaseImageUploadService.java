package amu.zhcet.storage.image.upload;

import amu.zhcet.firebase.storage.FirebaseStorageService;
import amu.zhcet.storage.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
@Slf4j
public class FirebaseImageUploadService {

    private final FirebaseStorageService firebaseStorageService;

    @Autowired
    public FirebaseImageUploadService(FirebaseStorageService firebaseStorageService) {
        this.firebaseStorageService = firebaseStorageService;
    }

    public CompletableFuture<Optional<String>> upload(Supplier<Image> imageSupplier) {
        return upload(imageSupplier.get());
    }

    public CompletableFuture<Optional<String>> upload(Image image) {
        try {
            return firebaseStorageService.uploadFile(image.getName(), image.getContentType(), image.getBytes());
        } catch (IOException e) {
            log.error("Avatar Error", e);
            throw new ImageUploadException(e.getMessage());
        }
    }

}
