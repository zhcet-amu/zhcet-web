package amu.zhcet.storage.file;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    String generateFileName(String name);

    String store(FileType fileType, File file);

    String store(FileType fileType, MultipartFile file);

    String store(FileType fileType, String name, byte[] bytes);

    Stream<Path> loadAll(FileType fileType);

    Path load(FileType fileType, String filename);

    Resource loadAsResource(FileType fileType, String filename);

    void delete(FileType fileType, String filename);

    void deleteAll(FileType fileType);

}