package in.ac.amu.zhcet.data.service.upload;

import com.j256.simplecsv.processor.CsvProcessor;
import com.j256.simplecsv.processor.ParseError;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.dto.AttendanceUpload;
import in.ac.amu.zhcet.data.service.RegisteredCourseService;
import in.ac.amu.zhcet.data.service.file.FileSystemStorageService;
import in.ac.amu.zhcet.data.service.file.StorageException;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AttendanceUploadService {

    private final RegisteredCourseService registeredCourseService;
    private final FileSystemStorageService systemStorageService;

    @Autowired
    public AttendanceUploadService(RegisteredCourseService registeredCourseService, FileSystemStorageService systemStorageService) {
        this.registeredCourseService = registeredCourseService;
        this.systemStorageService = systemStorageService;
    }

    @Data
    public static class UploadResult {
        @NonNull
        private List<String> errors = new ArrayList<>();
        @NonNull
        private List<AttendanceUpload> attendanceUploads = new ArrayList<>();
    }

    @Data
    public static class AttendanceConfirmation {
        @NonNull
        private Set<String> errors = new HashSet<>();
        @NonNull
        private Map<AttendanceUpload, Boolean> studentMap = new HashMap<>();
    }

    public UploadResult handleUpload(MultipartFile file) throws IOException {
        UploadResult uploadResult = new UploadResult();

        try {
            systemStorageService.store(file);
        } catch (StorageException storageException) {
            log.error(storageException.getMessage());
        }

        if (!file.getContentType().equals("text/csv")) {
            logAndError(uploadResult, "Uploaded file is not of CSV format");

            return uploadResult;
        }

        CsvProcessor<AttendanceUpload> csvProcessor = new CsvProcessor<>(AttendanceUpload.class);
        try {
            List<ParseError> parseErrors = new ArrayList<>();
            List<AttendanceUpload> attendanceUploads = csvProcessor.readAll(new InputStreamReader(file.getInputStream()), parseErrors);

            if (attendanceUploads != null)
                uploadResult.getAttendanceUploads().addAll(attendanceUploads);
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

    public AttendanceConfirmation confirmUpload(String course, UploadResult uploadResult) {
        AttendanceConfirmation attendanceConfirmation = new AttendanceConfirmation();
        for (AttendanceUpload upload : uploadResult.getAttendanceUploads()) {
            boolean exists = registeredCourseService.exists(upload.getStudent(),course);
            attendanceConfirmation.studentMap.put(upload, exists);
            if (!exists)
                attendanceConfirmation.errors.add("The students highlighted in red are not registered for this course");
        }
        return attendanceConfirmation;
    }

    @Transactional
    public void updateAttendance(String course, List<AttendanceUpload> uploadList) {
        for (AttendanceUpload attendance : uploadList) {
            CourseRegistration courseRegistration = registeredCourseService.getByStudentAndCourse(attendance.getStudent(), course);
            registeredCourseService.setAttendance(courseRegistration, attendance.getDelivered(), attendance.getAttended());
        }
    }

    private static void logAndError(UploadResult uploadResult, String error) {
        log.error(error);
        uploadResult.errors.add(error);
    }

}
