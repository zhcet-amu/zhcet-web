package in.ac.amu.zhcet.service.upload.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    String generateFileName(String name);

    void store(FileType fileType, File file);

    void store(FileType fileType, MultipartFile file);

    Stream<Path> loadAll(FileType fileType);

    Path load(FileType fileType, String filename);

    Resource loadAsResource(FileType fileType, String filename);

    void deleteAll(FileType fileType);

}