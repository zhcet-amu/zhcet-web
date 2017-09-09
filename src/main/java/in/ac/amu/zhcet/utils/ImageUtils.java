package in.ac.amu.zhcet.utils;

import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ImageUtils {

    public static float getRatio(BufferedImage image) {
        return ((float)image.getHeight())/image.getWidth();
    }

    public static BufferedImage crop(BufferedImage image, int pixels) {
        if (image == null)
            return null;
        float ratio = getRatio(image);
        if (Math.abs(ratio - 1) < 0.2)
            return image;
        log.info("Image Aspect Ratio " + ratio + " not within confines... Cropping...");
        pixels = Math.min(pixels, Math.min(image.getHeight(), image.getWidth()));
        log.info("Cropping image to largest square center crop : " + pixels + " pixels");
        return Scalr.crop(image, (image.getWidth() - pixels) / 2, (image.getHeight() - pixels) / 2, pixels, pixels);
    }

    public static InputStream generateThumbnail(BufferedImage image, String format, int pixels) throws IOException {
        if (image == null) throw new RuntimeException("Error opening image");

        log.info(String.format("Original Image Resolution : %dx%d", image.getHeight(), image.getWidth()));

        BufferedImage newImage = null;
        if (Math.max(image.getHeight(), image.getWidth()) > pixels) {
            log.info("Image larger than " + pixels + " pixels. Resizing...");
            Scalr.Mode mode = image.getHeight() > image.getWidth() ? Scalr.Mode.FIT_TO_WIDTH : Scalr.Mode.FIT_TO_HEIGHT;
            newImage = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, mode, pixels, pixels);
            log.info(String.format("New Image Resolution : %dx%d", newImage.getHeight(), newImage.getWidth()));
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

}
