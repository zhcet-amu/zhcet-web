package in.ac.amu.zhcet.service.core.upload.base;

import com.j256.simplecsv.processor.CsvProcessor;
import com.j256.simplecsv.processor.ParseError;
import in.ac.amu.zhcet.service.file.FileSystemStorageService;
import in.ac.amu.zhcet.service.file.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AbstractUploadService<T, U, V> {

    private final FileSystemStorageService systemStorageService;

    @Autowired
    public AbstractUploadService(FileSystemStorageService systemStorageService) {
        this.systemStorageService = systemStorageService;
    }

    private void storeFile(UploadResult<T> uploadResult, MultipartFile file) {
        try {
            systemStorageService.store(file);
        } catch (StorageException storageException) {
            uploadResult.getErrors().add(storageException.getMessage());
            log.error(String.format("Error storing file %s", file.getOriginalFilename()), storageException);
        }
    }

    private boolean validateType(MultipartFile file, UploadResult<T> uploadResult) {
        if (!file.getContentType().equals("text/csv")) {
            uploadResult.getErrors().add("Uploaded file is not of CSV format");
            log.warn("Uploaded file is not of CSV format {} {}", file.getOriginalFilename(), file.getContentType());
            return false;
        }

        return true;
    }

    private void parseFile(Class<T> uploadClass, MultipartFile file, UploadResult<T> uploadResult) throws IOException {
        CsvProcessor<T> csvProcessor = new CsvProcessor<>(uploadClass)
                .withAlwaysTrimInput(true)
                .withIgnoreUnknownColumns(true)
                .withFlexibleOrder(true);
        try {
            List<ParseError> parseErrors = new ArrayList<>();
            List<T> uploads = csvProcessor.readAll(new InputStreamReader(file.getInputStream()), parseErrors);

            if (uploads != null) {
                uploadResult.getUploads().addAll(uploads);
            }

            List<String> errors = parseErrors.stream().map(parseError -> {
                String message = parseError.getMessage().replace("suppled", "supplied") +
                        "<br>Line Number: " + parseError.getLineNumber() + " Position: " + parseError.getLinePos();

                if (parseError.getLine() != null)
                    message += "<br>Line: " + parseError.getLine();

                return message;
            }).collect(Collectors.toList());
            log.warn(String.format("CSV Parsing Errors %s", file.getOriginalFilename()), errors.toString());
            uploadResult.getErrors().addAll(errors);
        } catch (ParseException e) {
            log.error(String.format("Error Parsing file %s", file.getOriginalFilename()), e);
            uploadResult.getErrors().add(e.getMessage());
        }
    }

    public UploadResult<T> handleUpload(Class<T> uploadClass, MultipartFile file) throws IOException {
        UploadResult<T> uploadResult = new UploadResult<>();

        storeFile(uploadResult, file);
        if (validateType(file, uploadResult))
            parseFile(uploadClass, file, uploadResult);

        return uploadResult;
    }

    public Confirmation<U, V> confirmUpload(UploadResult<T> uploadResult, Function<T, U> converter, Function<U, V> infer) {
        Confirmation<U, V> confirmation = new Confirmation<>();

        uploadResult
                .getUploads()
                .stream()
                .map(converter)
                .forEach(item -> confirmation.getData().put(item, infer.apply(item)));

        return confirmation;
    }

}
