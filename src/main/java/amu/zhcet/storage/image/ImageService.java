package amu.zhcet.storage.image;

import amu.zhcet.common.utils.ImageUtils;
import amu.zhcet.common.utils.Utils;
import amu.zhcet.firebase.storage.FirebaseStorageService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class ImageService {

    private static final int MAX_FILE_SIZE = 2*1024*1024;
    private static final int ORIGINAL_AVATAR_SIZE = 1000;
    private static final int AVATAR_SIZE = 86;

    private final FirebaseStorageService firebaseStorageService;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Avatar {
        private String avatarUrl;
        private String originalAvatarUrl;
    }

    @Autowired
    public ImageService(FirebaseStorageService firebaseStorageService) {
        this.firebaseStorageService = firebaseStorageService;
    }

    private boolean verifyType(String fileName, boolean contentType) {
        if (fileName != null && !fileName.isEmpty() && (contentType || fileName.contains("."))) {
            final String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
            String[] allowedExt = { "jpg", "jpeg", "png", "gif", "bmp" };
            for (String s : allowedExt) {
                String allowed = (contentType ? "image/" : "") + s;
                if (extension.equals(allowed)) {
                    return true;
                }
            }
        }
        return false;
    }

    private float getRatio(BufferedImage image) {
        return ((float)image.getHeight())/image.getWidth();
    }

    private BufferedImage crop(BufferedImage image, int pixels) {
        if (image == null)
            return null;
        float ratio = getRatio(image);
        if (Math.abs(ratio - 1) < 0.2)
            return image;
        log.warn("Image Aspect Ratio {} not within confines... Cropping...", ratio);
        pixels = Math.min(pixels, Math.min(image.getHeight(), image.getWidth()));
        log.warn("Cropping image to largest square center crop : {} pixels", pixels);
        return Scalr.crop(image, (image.getWidth() - pixels) / 2, (image.getHeight() - pixels) / 2, pixels, pixels);
    }

    private InputStream generateThumbnail(BufferedImage image, String format, int pixels) throws IOException {
        log.info("Original Image Resolution : {}x{}", image.getHeight(), image.getWidth());

        BufferedImage newImage = null;
        if (Math.max(image.getHeight(), image.getWidth()) > pixels) {
            log.warn("Image larger than {} pixels. Resizing...", pixels);
            Scalr.Mode mode = image.getHeight() > image.getWidth() ? Scalr.Mode.FIT_TO_WIDTH : Scalr.Mode.FIT_TO_HEIGHT;
            newImage = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, mode, pixels, pixels);
            log.warn("New Image Resolution : {}x{}", newImage.getHeight(), newImage.getWidth());
        }

        newImage = crop(newImage, pixels);
        if (newImage == null)
            newImage = crop(image, pixels);

        if (newImage == null)
            return null;

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(newImage, format, os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    private InputStream getNormalizedImage(MultipartFile file, String format, Integer size) throws IOException {
        if (file.getSize() > MAX_FILE_SIZE) {
            log.warn("Image larger than 2 MB : {}", file.getOriginalFilename());
            throw new ImageUploadException("File should be smaller than 2 MB");
        }

        BufferedImage image = ImageUtils.readImage(file);
        if (image == null || !verifyType(file.getOriginalFilename(), false) || !verifyType(file.getContentType(), true)) {
            log.warn("Image should be of valid type : {}", file.getOriginalFilename());
            throw new ImageUploadException("File type must be image, found " + file.getContentType());
        }

        log.warn("Original Image Size : {}", Utils.humanReadableByteCount(file.getSize(), true));

        InputStream toUpload = file.getInputStream();
        if (size != null) {
            log.warn("Resizing image to size : {}", size);
            InputStream resizedImage = generateThumbnail(image, format, size);

            if (resizedImage != null)
                toUpload = resizedImage;
        } else {
            log.warn("Not resizing image");
        }

        return toUpload;
    }

    public CompletableFuture<Optional<String>> upload(String pathWithoutExtension, MultipartFile file, Integer size) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        try {
            InputStream toUpload = getNormalizedImage(file, extension, size);
            return firebaseStorageService.uploadFile(pathWithoutExtension + "." + extension, file.getContentType(), toUpload);
        } catch (IOException e) {
            log.error("Avatar Error", e);
            throw new ImageUploadException(e.getMessage());
        }
    }

    public Avatar uploadAvatar(String pathWithoutExtension, MultipartFile file) throws ExecutionException, InterruptedException {
        CompletableFuture<Optional<String>> originalAvatarLink = upload(pathWithoutExtension, file, ORIGINAL_AVATAR_SIZE);
        CompletableFuture<Optional<String>> avatarLink = upload(pathWithoutExtension + "_thumb", file, AVATAR_SIZE);

        CompletableFuture.allOf(originalAvatarLink, avatarLink).join();
        return new Avatar(avatarLink.get().get(), originalAvatarLink.get().get());
    }

}
