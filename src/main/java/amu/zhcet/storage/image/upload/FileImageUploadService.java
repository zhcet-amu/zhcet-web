package amu.zhcet.storage.image.upload;

import amu.zhcet.storage.file.FileSystemStorageService;
import amu.zhcet.storage.file.FileType;
import amu.zhcet.storage.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Slf4j
@Service
public class FileImageUploadService {

    private static final int FILENAME_COUNT = 64;
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final FileSystemStorageService fileSystemStorageService;
    private final UploadedImageRepository uploadedImageRepository;

    @Autowired
    public FileImageUploadService(FileSystemStorageService fileSystemStorageService, UploadedImageRepository uploadedImageRepository) {
        this.fileSystemStorageService = fileSystemStorageService;
        this.uploadedImageRepository = uploadedImageRepository;
    }

    public String upload(Image image) {
        return uploadImage(image).getUrl();
    }

    UploadedImage uploadImage(Image image) {
        String id = getUniqueFileName(image.getExtension());
        String stored = fileSystemStorageService.store(FileType.IMAGE, id, image.getBytes());
        String url = "/image:view/" + id;

        UploadedImage uploadedImage = new UploadedImage();
        uploadedImage.setId(id);
        uploadedImage.setFilename(stored);
        uploadedImage.setUrl(url);
        uploadedImage.setOriginalFilename(image.getName());
        uploadedImage.setContentType(image.getContentType());
        uploadedImage.setThumbnail(image.isThumbnail());

        uploadedImageRepository.save(uploadedImage);

        return uploadedImage;
    }

    public void delete(UploadedImage image) {
        if (image == null) {
            log.warn("Trying to delete unknown image");
        } else {
            fileSystemStorageService.delete(FileType.IMAGE, image.getFilename());
            uploadedImageRepository.delete(image);
        }
    }

    void save(UploadedImage uploadedImage) {
        uploadedImageRepository.save(uploadedImage);
    }

    private String getUniqueFileName(String extension) {
        String filename = randomAlphaNumeric(FILENAME_COUNT) + "." + extension;

        while (fileExists(filename)) {
            filename = randomAlphaNumeric(FILENAME_COUNT);
        }

        return filename;
    }

    private boolean fileExists(String filename) {
        Path path = fileSystemStorageService.load(FileType.IMAGE, filename);
        return path.toFile().exists();
    }

    private static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

}
