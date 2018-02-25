package amu.zhcet.storage.image;

import amu.zhcet.storage.image.edit.ImageEditException;
import amu.zhcet.storage.image.upload.ImageUpload;
import amu.zhcet.storage.image.upload.ImageUploadException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
public class ImageUtils {

    private static final int MAX_FILE_SIZE = 2*1024*1024;

    // Prevent instantiation of Util class
    private ImageUtils() {}

    public static boolean isUnallowedType(String fileName, boolean contentType) {
        if (fileName != null && !fileName.isEmpty() && (contentType || fileName.contains("."))) {
            final String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
            String[] allowedExt = { "jpg", "jpeg", "png", "gif", "bmp" };
            for (String s : allowedExt) {
                String allowed = (contentType ? "image/" : "") + s;
                if (extension.equals(allowed)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void checkImageSize(ImageUpload imageUpload) {
        if (imageUpload.getSize() > MAX_FILE_SIZE) {
            log.debug("Image larger than 2 MB : {}", imageUpload.getName());
            throw new ImageUploadException("File should be smaller than 2 MB");
        }
    }

    public static void checkImageFormat(ImageUpload imageUpload) {
        if (isUnallowedType(imageUpload.getName(), false) || isUnallowedType(imageUpload.getContentType(), true)) {
            log.debug("Image should be of valid type : {}", imageUpload.getName());
            throw new ImageUploadException("File type must be image, found " + imageUpload.getContentType());
        }
    }

    public static void requireValidImage(ImageUpload imageUpload) {
        checkImageSize(imageUpload);
        checkImageFormat(imageUpload);
    }

    public static void requireValidImage(MultipartFile file) {
        requireValidImage(fromMultipartFile(file));
    }

    public static ImageUpload fromMultipartFile(MultipartFile file) {
        ImageUpload imageUpload = new ImageUpload();
        try {
            imageUpload.setBytes(file.getBytes());
            imageUpload.setInputStream(file.getInputStream());
        } catch (IOException e) {
            throw new ImageEditException("Cannot map input stream to ImageUpload instance");
        }
        imageUpload.setExtension(FilenameUtils.getExtension(file.getOriginalFilename()));
        imageUpload.setName(file.getOriginalFilename());
        imageUpload.setSize(file.getSize());
        imageUpload.setContentType(file.getContentType());
        return imageUpload;
    }

}
