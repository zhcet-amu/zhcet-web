package in.ac.amu.zhcet.data.service.upload;

import com.j256.simplecsv.processor.CsvProcessor;
import com.j256.simplecsv.processor.ParseError;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.dto.FacultyUpload;
import in.ac.amu.zhcet.data.repository.DepartmentRepository;
import in.ac.amu.zhcet.data.service.FacultyService;
import in.ac.amu.zhcet.data.service.file.FileSystemStorageService;
import in.ac.amu.zhcet.data.service.file.StorageException;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FacultyUploadService {
    private final DepartmentRepository departmentRepository;
    private final FacultyService facultyService;
    private final FileSystemStorageService systemStorageService;
    private final static int PASS_LENGTH = 6;

    @Autowired
    public FacultyUploadService(DepartmentRepository departmentRepository, FacultyService facultyService, FileSystemStorageService fileSystemStorageService) {
        this.departmentRepository = departmentRepository;
        this.facultyService = facultyService;
        this.systemStorageService = fileSystemStorageService;
    }

    @Data
    public static class UploadResult {
        @NonNull
        private List<String> errors = new ArrayList<>();
        @NonNull
        private List<FacultyUpload> facultyUploads = new ArrayList<>();
    }

    @Data
    public static class FacultyConfirmation {
        @NonNull
        private Set<String> errors = new HashSet<>();
        @NonNull
        private Map<FacultyMember, String> facultyMap = new HashMap<>();
    }

    public FacultyUploadService.UploadResult handleUpload(MultipartFile file) throws IOException {
        FacultyUploadService.UploadResult uploadResult = new FacultyUploadService.UploadResult();

        try {
            systemStorageService.store(file);
        } catch (StorageException storageException) {

            log.error(storageException.getMessage());
        }
        if (!file.getContentType().equals("text/csv")) {
            logAndError(uploadResult, "Uploaded file is not of CSV format");
            log.info(file.getContentType());
            return uploadResult;
        }

        CsvProcessor<FacultyUpload> csvProcessor = new CsvProcessor<>(FacultyUpload.class);
        try {
            List<ParseError> parseErrors = new ArrayList<>();
            List<FacultyUpload> facultyUploads = csvProcessor.readAll(new InputStreamReader(file.getInputStream()), parseErrors);
            if (facultyUploads != null) {
                uploadResult.getFacultyUploads().addAll(facultyUploads);

                log.info(facultyUploads.toString());
            }
            uploadResult.getErrors().addAll(
                    parseErrors.stream().map(parseError -> parseError.getMessage() +
                            "<br>Line Number: " + parseError.getLineNumber() + " Position: " + parseError.getLinePos() +
                            "<br>Line: " + parseError.getLine()).collect(Collectors.toList()));
        } catch (ParseException e) {
            e.printStackTrace();
            uploadResult.getErrors().add(e.getMessage());
        }
        return uploadResult;
    }


    public FacultyConfirmation confirmUpload(UploadResult uploadResult) {
        FacultyUploadService.FacultyConfirmation facultyConfirmation = new FacultyUploadService.FacultyConfirmation();
        List<Department> departments = departmentRepository.findAll();

        for (FacultyUpload facultyUpload : uploadResult.getFacultyUploads()) {
            String password = generatePassword(PASS_LENGTH);
            facultyUpload.setPassword(password);
            FacultyMember facultyMember = new FacultyMember();
            facultyMember.setFacultyId(capitalizeAll(facultyUpload.getFacultyId()));
            facultyMember.getUser().setName(capitalizeFirst(facultyUpload.getName()));
            String departmentName = capitalizeFirst(facultyUpload.getDepartment());
            Optional<Department> optional = departments.stream()
                    .filter(department -> department.getName().equals(departmentName))
                    .findFirst();

            if (!optional.isPresent()) {
                facultyConfirmation.facultyMap.put(facultyMember, "No such department: " + departmentName);
                facultyConfirmation.errors.add("Faculty Member with invalid department found");
            } else if (facultyService.getById(facultyMember.getFacultyId()) != null) {
                facultyConfirmation.facultyMap.put(facultyMember, "Duplicate Faculty ID");
                facultyConfirmation.errors.add("Faculty Member with duplicate Faculty ID found");
            } else {
                facultyMember.getUser().getDetails().setDepartment(optional.get());
                facultyMember.getUser().setPassword(password);
                facultyConfirmation.facultyMap.put(facultyMember, null);
            }

        }
        return facultyConfirmation;
    }

    @Transactional
    public String registerFaculty(FacultyUploadService.FacultyConfirmation confirmation) throws IOException {
        String filename = saveFile(confirmation);

        for (FacultyMember facultyMember : confirmation.getFacultyMap().keySet()) {
            facultyService.register(facultyMember);
        }

        return filename;
    }

    private String saveFile(FacultyUploadService.FacultyConfirmation confirmation) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LocalDateTime localDateTime = LocalDateTime.now();
        String filename = StringUtils.cleanPath(localDateTime.toString() + "_" + username + "_" + "_faculty_password.csv");

        Path filePath = systemStorageService.load(filename);
        File newFile = filePath.toFile();

        List<FacultyUpload> facultyUploads = confirmation.facultyMap.keySet().stream()
                .map(FacultyUploadService::fromFaculty)
                .collect(Collectors.toList());

        CsvProcessor<FacultyUpload> csvProcessor = new CsvProcessor<>(FacultyUpload.class);
        csvProcessor.writeAll(newFile, facultyUploads, true);

        return filename;
    }

    private static FacultyUpload fromFaculty(FacultyMember facultyMember) {
        FacultyUpload facultyUpload = new FacultyUpload();
        facultyUpload.setFacultyId(facultyMember.getFacultyId());
        facultyUpload.setName(facultyMember.getUser().getName());
        facultyUpload.setDepartment(facultyMember.getUser().getDetails().getDepartment().getName());
        facultyUpload.setPassword(facultyMember.getUser().getPassword());

        return facultyUpload;
    }

    private static String capitalizeFirst(String string) {
        return WordUtils.capitalizeFully(string.trim());
    }

    private static String capitalizeAll(String string) {
        return string.trim().toUpperCase(Locale.getDefault());
    }


    private static void logAndError(FacultyUploadService.UploadResult uploadResult, String error) {
        log.error(error);
        uploadResult.errors.add(error);
    }

    private String generatePassword(int length){
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "").substring(0,length);
    }
}
