package in.ac.amu.zhcet.data.service.upload;

import com.j256.simplecsv.processor.CsvProcessor;
import com.j256.simplecsv.processor.ParseError;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.RegistrationUpload;
import in.ac.amu.zhcet.data.service.FloatedCourseService;
import in.ac.amu.zhcet.data.service.StudentService;
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
public class RegistrationUploadService {

    private final StudentService studentService;
    private final FloatedCourseService floatedCourseService;
    private final FileSystemStorageService systemStorageService;

    @Autowired
    public RegistrationUploadService(StudentService studentService, FloatedCourseService floatedCourseService, FileSystemStorageService systemStorageService) {
        this.studentService = studentService;
        this.floatedCourseService = floatedCourseService;
        this.systemStorageService = systemStorageService;
    }

    @Data
    public static class UploadResult {
        @NonNull
        private List<String> errors = new ArrayList<>();
        @NonNull
        private List<RegistrationUpload> registrationUploads = new ArrayList<>();
    }

    @Data
    public static class RegistrationConfirmation {
        @NonNull
        private Set<String> errors = new HashSet<>();
        @NonNull
        private Map<Student, String> studentMap = new HashMap<>();
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

        CsvProcessor<RegistrationUpload> csvProcessor = new CsvProcessor<>(RegistrationUpload.class);
        try {
            List<ParseError> parseErrors = new ArrayList<>();
            List<RegistrationUpload> registrationUploads = csvProcessor.readAll(new InputStreamReader(file.getInputStream()), parseErrors);

            if (registrationUploads != null) {
                uploadResult.getRegistrationUploads().addAll(registrationUploads);

                log.info(registrationUploads.toString());
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

    private static String capitalizeAll(String string) {
        return string.trim().toUpperCase(Locale.getDefault());
    }

    public RegistrationConfirmation confirmUpload(String courseId, UploadResult uploadResult) {
        RegistrationConfirmation registrationConfirmation = new RegistrationConfirmation();
        List<CourseRegistration> registrations = floatedCourseService.getCourseById(courseId).getCourseRegistrations();

        for (RegistrationUpload registrationUpload : uploadResult.getRegistrationUploads()) {
            Student student = studentService.getByEnrolmentNumber(capitalizeAll(registrationUpload.getEnrolmentNo()));

            if (student == null) {
                registrationConfirmation.studentMap.put(Student.builder().enrolmentNumber(registrationUpload.getEnrolmentNo()).build(), "No such student found");
                registrationConfirmation.errors.add("Invalid student enrolment number found");
            } else if(registrations.stream()
                    .map(CourseRegistration::getStudent)
                    .anyMatch(oldStudent -> oldStudent.equals(student))) {
                registrationConfirmation.studentMap.put(student, "Already enrolled in " + courseId);
                registrationConfirmation.errors.add("Students already enrolled in course found");
            } else {
                registrationConfirmation.studentMap.put(student, null);
            }
        }

        return registrationConfirmation;
    }

    @Transactional
    public void registerStudents(String courseId, List<String> studentIds) {
        floatedCourseService.registerStudents(courseId, studentIds);
    }

    private static void logAndError(UploadResult uploadResult, String error) {
        log.error(error);
        uploadResult.errors.add(error);
    }
}
