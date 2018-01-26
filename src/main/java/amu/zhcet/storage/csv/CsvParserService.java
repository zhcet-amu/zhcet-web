package amu.zhcet.storage.csv;

import amu.zhcet.common.model.Meta;
import amu.zhcet.storage.FileSystemStorageService;
import amu.zhcet.storage.FileType;
import amu.zhcet.storage.StorageException;
import com.j256.simplecsv.processor.ParseError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Named;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class CsvParserService<T, U extends Meta> {

    private final FileSystemStorageService systemStorageService;
    private final List<String> allowedCsvTypes;

    @Autowired
    public CsvParserService(@Named("allowedCsvTypes") List<String> allowedCsvTypes, FileSystemStorageService systemStorageService) {
        this.systemStorageService = systemStorageService;
        this.allowedCsvTypes = allowedCsvTypes;
    }

    public UploadResult<T> handleUpload(Class<T> uploadClass, MultipartFile file) throws IOException {
        UploadResult<T> uploadResult = new UploadResult<>();

        boolean fileStored = storeFile(file, uploadResult);
        boolean fileTypeValid = validateType(file, uploadResult);
        if (fileStored && fileTypeValid)
            parseFile(uploadClass, file, uploadResult);

        return uploadResult;
    }

    public ConfirmationAdapter<T, U> confirmUpload(UploadResult<T> uploadResult) {
        return new ConfirmationAdapter<>(uploadResult);
    }

    private boolean storeFile(MultipartFile file, UploadResult<T> uploadResult) {
        try {
            systemStorageService.store(FileType.CSV, file);
            return true;
        } catch (StorageException fileException) {
            uploadResult.getErrors().add(fileException.getMessage());
            log.error(String.format("Error storing file %s", file.getOriginalFilename()), fileException);
            return false;
        }
    }

    private boolean validateType(MultipartFile file, UploadResult<T> uploadResult) {
        if (!allowedCsvTypes.contains(file.getContentType())) {
            uploadResult.getErrors().add("Uploaded file is not of CSV format");
            log.warn("Uploaded file is not of CSV format {} {}", file.getOriginalFilename(), file.getContentType());
            return false;
        }

        return true;
    }

    private static String getErrorMessage(ParseError parseError) {
        String message = parseError.getMessage().replace("suppled", "supplied") + // TODO: Correct the typo in library
                "<br>Line Number: " + parseError.getLineNumber() +
                " Position: " + parseError.getLinePos();

        if (parseError.getLine() != null)
            message += "<br>Line: " + parseError.getLine();

        return message;
    }

    private void parseFile(Class<T> uploadClass, MultipartFile file, UploadResult<T> uploadResult) throws IOException {
        CsvParser.Result<T> result = CsvParser.of(uploadClass).parse(file);

        uploadResult.getUploads().addAll(result.getItems());
        if (!result.isParsedSuccessfully()) {
            result.getParseErrors()
                    .stream()
                    .map(CsvParserService::getErrorMessage)
                    .forEach(message -> uploadResult.getErrors().add(message));

            log.warn(String.format("CSV Parsing Errors %s %s", file.getOriginalFilename(), uploadResult.getErrors()));
        }
    }

}
