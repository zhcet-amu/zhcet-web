package in.ac.amu.zhcet.service.upload.csv;

import com.j256.simplecsv.processor.ParseError;
import in.ac.amu.zhcet.data.model.base.Meta;
import in.ac.amu.zhcet.service.upload.storage.FileSystemStorageService;
import in.ac.amu.zhcet.service.upload.storage.FileType;
import in.ac.amu.zhcet.service.upload.storage.StorageException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Named;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
public class AbstractUploadService<T, U extends Meta> {

    private final FileSystemStorageService systemStorageService;
    private final List<String> allowedCsvTypes;

    @Data
    @RequiredArgsConstructor
    public static class ParseErrorWrapper {
        private final ParseError parseError;
        private final List<String> columns;
    }

    @Autowired
    public AbstractUploadService(@Named("allowedCsvTypes") List<String> allowedCsvTypes, FileSystemStorageService systemStorageService) {
        this.systemStorageService = systemStorageService;
        this.allowedCsvTypes = allowedCsvTypes;
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

    private static ParseErrorWrapper fromParseError(ParseError parseError, CsvParser.Result<?> result) {
        return new ParseErrorWrapper(parseError, result.getColumns());
    }

    private void parseFile(
            @Nonnull
            Class<T> uploadClass,
            @Nonnull
            MultipartFile file,
            @Nonnull
            UploadResult<T> uploadResult,
            @Nullable
            Function<ParseErrorWrapper, String> errorHandler
    ) throws IOException {
        CsvParser.Result<T> result = CsvParser.of(uploadClass, file).parse();

        Function<ParseErrorWrapper, String> handler = errorHandler != null ? errorHandler : CsvParseErrorHandler::handleError;

        uploadResult.getUploads().addAll(result.getItems());
        if (!result.isParsedSuccessfully()) {
            result.getParseErrors()
                    .stream()
                    .map(parseError -> fromParseError(parseError, result))
                    .map(handler)
                    .forEach(message -> uploadResult.getErrors().add(message));

            log.warn(String.format("CSV Parsing Errors %s %s", file.getOriginalFilename(), uploadResult.getErrors()));
        }
    }

    public UploadResult<T> handleUpload(Class<T> uploadClass, MultipartFile file) throws IOException {
        return handleUpload(uploadClass, file, null);
    }

    public UploadResult<T> handleUpload(
            Class<T> uploadClass,
            MultipartFile file,
            Function<ParseErrorWrapper,String> errorHandler
    )
            throws IOException
    {
        UploadResult<T> uploadResult = new UploadResult<>();

        if (storeFile(file, uploadResult) && validateType(file, uploadResult))
            parseFile(uploadClass, file, uploadResult, errorHandler);

        return uploadResult;
    }

    public ConfirmationAdapter<T, U> confirmUpload(UploadResult<T> uploadResult) {
        return new ConfirmationAdapter<>(uploadResult);
    }

}
