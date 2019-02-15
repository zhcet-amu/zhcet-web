package amu.zhcet.storage.csv.neo;

import amu.zhcet.storage.FileSystemStorageService;
import amu.zhcet.storage.FileType;
import amu.zhcet.storage.StorageException;
import amu.zhcet.storage.csv.CsvParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class FileStorageCsvParser {

    private final FileSystemStorageService systemStorageService;
    private final List<String> allowedCsvTypes;

    public FileStorageCsvParser(FileSystemStorageService systemStorageService, List<String> allowedCsvTypes) {
        this.systemStorageService = systemStorageService;
        this.allowedCsvTypes = allowedCsvTypes;
    }

    public <T> Result<T> parse(Class<T> resultClass, MultipartFile file) throws IOException {
        Result<T> uploadResult = new Result<>();

        boolean fileStored = storeFile(file, uploadResult);
        boolean fileTypeValid = validateType(file, uploadResult);
        if (fileStored && fileTypeValid) {
            uploadResult.setCsv(CsvParser.of(resultClass).parse(file));
            uploadResult.setParsed(true);
        }
        return uploadResult;
    }

    private <T> boolean storeFile(MultipartFile file, Result<T> uploadResult) {
        try {
            systemStorageService.store(FileType.CSV, file);
            return true;
        } catch (StorageException fileException) {
            uploadResult.getMessages().add(Message.error("Could not store file"));
            log.error(String.format("Error storing file %s", file.getOriginalFilename()), fileException);
            return false;
        }
    }

    private <T> boolean validateType(MultipartFile file, Result<T> uploadResult) {
        if (!allowedCsvTypes.contains(file.getContentType())) {
            uploadResult.getMessages().add(Message.error("Uploaded file is not of CSV format"));
            log.warn("Uploaded file is not of CSV format {} {}", file.getOriginalFilename(), file.getContentType());
            return false;
        }

        return true;
    }

}
