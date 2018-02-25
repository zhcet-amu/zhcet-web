package amu.zhcet.storage.image.edit;

import amu.zhcet.common.utils.Utils;
import amu.zhcet.storage.image.Image;
import amu.zhcet.storage.image.upload.ImageUpload;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static amu.zhcet.storage.image.ImageUtils.fromMultipartFile;

@Slf4j
@Service
public class ImageEditService {

    public CompletableFuture<Image> normalizeAsync(MultipartFile file, Integer size) {
        return CompletableFuture.completedFuture(normalize(file, size));
    }

    public Image normalize(MultipartFile file, Integer size) {
        return normalize(fromMultipartFile(file), size);
    }

    public Image normalize(ImageUpload imageUpload, Integer size) {
        try {
            byte[] bytes = getNormalizedImage(imageUpload, size);
            Image image = new Image();
            image.setBytes(bytes);
            image.setContentType(imageUpload.getContentType());
            image.setName(imageUpload.getName());
            image.setExtension(imageUpload.getExtension());
            return image;
        } catch (IOException e) {
            log.error("Error normalizing image", e);
            throw new ImageEditException(e.getMessage());
        }
    }

    private byte[] getNormalizedImage(ImageUpload imageUpload, Integer size) throws IOException {
        BufferedImage image = ImageIO.read(imageUpload.getInputStream());

        log.debug("Original Image Size : {}", Utils.humanReadableByteCount(imageUpload.getSize(), true));

        if (size != null) {
            log.debug("Resizing image to size : {}", size);
            byte[] resizedImage = generateThumbnail(image, imageUpload.getExtension(), size);

            if (resizedImage != null)
                return resizedImage;
            else
                return imageUpload.getBytes();
        } else {
            log.debug("Not resizing image");
            return imageUpload.getBytes();
        }
    }

    private byte[] generateThumbnail(BufferedImage image, String format, int pixels) throws IOException {
        log.debug("Original Image Resolution : {}x{}", image.getHeight(), image.getWidth());

        BufferedImage newImage = null;
        if (Math.max(image.getHeight(), image.getWidth()) > pixels) {
            log.debug("Image larger than {} pixels. Resizing...", pixels);
            Scalr.Mode mode = image.getHeight() > image.getWidth() ? Scalr.Mode.FIT_TO_WIDTH : Scalr.Mode.FIT_TO_HEIGHT;
            newImage = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, mode, pixels, pixels);
            log.debug("New Image Resolution : {}x{}", newImage.getHeight(), newImage.getWidth());
        }

        newImage = crop(newImage, pixels);
        if (newImage == null)
            newImage = crop(image, pixels);

        if (newImage == null)
            return null;

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(newImage, format, os);
        return os.toByteArray();
    }

    private BufferedImage crop(BufferedImage image, int pixels) {
        if (image == null)
            return null;
        float ratio = getRatio(image);
        if (Math.abs(ratio - 1) < 0.2)
            return image;
        log.debug("Image Aspect Ratio {} not within confines... Cropping...", ratio);
        pixels = Math.min(pixels, Math.min(image.getHeight(), image.getWidth()));
        log.debug("Cropping image to largest square center crop : {} pixels", pixels);
        return Scalr.crop(image, (image.getWidth() - pixels) / 2, (image.getHeight() - pixels) / 2, pixels, pixels);
    }

    private float getRatio(BufferedImage image) {
        return ((float)image.getHeight())/image.getWidth();
    }

}
