package in.ac.amu.zhcet.service.misc;

import in.ac.amu.zhcet.utils.ImageUtils;
import in.ac.amu.zhcet.utils.Utils;
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

@Slf4j
@Service
public class ImageService {

    private static final int MAX_FILE_SIZE = 2*1024*1024;
    private static final int ORIGINAL_AVATAR_SIZE = 1000;
    private static final int AVATAR_SIZE = 86;

    private final FirebaseService firebaseService;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Avatar {
        private String avatarUrl;
        private String originalAvatarUrl;
    }

    @Autowired
    public ImageService(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
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

    public float getRatio(BufferedImage image) {
        return ((float)image.getHeight())/image.getWidth();
    }

    public BufferedImage crop(BufferedImage image, int pixels) {
        if (image == null)
            return null;
        float ratio = getRatio(image);
        if (Math.abs(ratio - 1) < 0.2)
            return image;
        log.warn("Image Aspect Ratio " + ratio + " not within confines... Cropping...");
        pixels = Math.min(pixels, Math.min(image.getHeight(), image.getWidth()));
        log.warn("Cropping image to largest square center crop : " + pixels + " pixels");
        return Scalr.crop(image, (image.getWidth() - pixels) / 2, (image.getHeight() - pixels) / 2, pixels, pixels);
    }

    public InputStream generateThumbnail(BufferedImage image, String format, int pixels) throws IOException {
        log.info("Original Image Resolution : %dx%d", image.getHeight(), image.getWidth());

        BufferedImage newImage = null;
        if (Math.max(image.getHeight(), image.getWidth()) > pixels) {
            log.warn("Image larger than " + pixels + " pixels. Resizing...");
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

    public String upload(String pathWithoutExtension, MultipartFile file, Integer size) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if (file.getSize() > MAX_FILE_SIZE) {
            log.warn("Image larger than 2 MB : {}", file.getOriginalFilename());
            throw new ImageUploadException("File should be smaller than 2 MB");
        }

        try {
            BufferedImage image = ImageUtils.readImage(file);
            if (image == null || !verifyType(file.getOriginalFilename(), false) || !verifyType(file.getContentType(), true)) {
                log.warn("Image should be of valid type : {}", file.getOriginalFilename());
                throw new ImageUploadException("File type must be image, found " + file.getContentType());
            }

            log.warn(String.format("Original Image Size : %s", Utils.humanReadableByteCount(file.getSize(), true)));

            InputStream toUpload = file.getInputStream();
            if (size != null) {
                log.warn("Resizing image to size : {}", size);
                InputStream resizedImage = generateThumbnail(image, extension, size);

                if (resizedImage != null)
                    toUpload = resizedImage;
            } else {
                log.warn("Not resizing image");
            }

            return firebaseService.uploadFile(pathWithoutExtension + "." + extension, file.getContentType(), toUpload);
        } catch (IOException e) {
            log.error("Avatar Error", e);
            throw new ImageUploadException(e.getMessage());
        }
    }

    public Avatar uploadAvatar(String pathWithoutExtension, MultipartFile file) {
        String originalAvatarLink = upload(pathWithoutExtension, file, ORIGINAL_AVATAR_SIZE);
        String avatarLink = upload(pathWithoutExtension + "_thumb", file, AVATAR_SIZE);

        return new Avatar(avatarLink, originalAvatarLink);
    }

}
