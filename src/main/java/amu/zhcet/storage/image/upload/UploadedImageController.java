package amu.zhcet.storage.image.upload;

import amu.zhcet.storage.FileSystemStorageService;
import amu.zhcet.storage.FileType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Slf4j
@Controller
class UploadedImageController {

    private final FileSystemStorageService systemStorageService;

    @Autowired
    public UploadedImageController(FileSystemStorageService systemStorageService) {
        this.systemStorageService = systemStorageService;
    }

    @ResponseBody
    @GetMapping("/image:view/{image:.+}")
    public ResponseEntity<InputStreamResource> serveImage(@PathVariable UploadedImage image) {
        if (image == null) {
            log.warn("Trying to view non existent image");
            return ResponseEntity.notFound().build();
        }

        File file = systemStorageService.load(FileType.IMAGE, image.getFilename()).toFile();
        if (!file.exists()) {
            log.warn("Trying to view non existent image {}", image);
            return ResponseEntity.notFound().build();
        }

        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(image.getContentType()))
                    .contentLength(file.length())
                    .body(new InputStreamResource(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
    }

}
