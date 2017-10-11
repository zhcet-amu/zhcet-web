package in.ac.amu.zhcet.service.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.stream.Stream;

@Slf4j
@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public String generateFileName(String name) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LocalDateTime localDateTime = LocalDateTime.now();

        return StringUtils.cleanPath(localDateTime.toString() + "_" + username + "_" + name);
    }

    private void storeAbstract(String name, boolean empty, InputStream inputStream) {
        String filename = generateFileName(name);
        try {
            if (empty) {
                throw new StorageException("Failed to store empty file " + filename);
            }

            if (filename.contains("..")) {
                String message = "Cannot store file with relative path outside current directory " + filename;
                // This is a security check
                log.warn(message);
                throw new StorageException(message);
            }
            Files.copy(inputStream, this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            log.info("Saved file " + filename);
        } catch (IOException e) {
            log.error(String.format("Failed storing file %s", filename), e);
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    @Override
    public void store(File file) {
        try {
            storeAbstract(file.getName(), file.length() == 0, new FileInputStream(file));
        } catch (FileNotFoundException e) {
            log.error(String.format("Failed storing file %s", file.getName()), e);
            throw new StorageException("Failed to store file " + file.getName(), e);
        }
    }

    @Override
    public void store(MultipartFile file) {
        try {
            storeAbstract(file.getOriginalFilename(), file.isEmpty(), file.getInputStream());
        } catch (IOException e) {
            log.error(String.format("Failed storing file %s", file.getName()), e);
            throw new StorageException("Failed to store file " + file.getName(), e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            log.error("Failed to read files", e);
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                log.error("Failed to read file %s", filename);
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }
        }
        catch (MalformedURLException e) {
            log.error(String.format("Failed to read file %s", filename), e);
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            log.error("Could not initialize storage", e);
            throw new StorageException("Could not initialize storage", e);
        }
    }
}