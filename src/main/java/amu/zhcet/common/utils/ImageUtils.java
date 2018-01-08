package amu.zhcet.common.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageUtils {

    // Prevent instantiation of Util class
    private ImageUtils() {}

    public static BufferedImage readImage(MultipartFile file) {
        try {
            return ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            return null;
        }
    }

}
