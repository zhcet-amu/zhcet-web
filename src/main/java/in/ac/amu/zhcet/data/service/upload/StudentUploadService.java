package in.ac.amu.zhcet.data.service.upload;

import com.j256.simplecsv.processor.CsvProcessor;
import com.j256.simplecsv.processor.ParseError;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.AttendanceUpload;
import in.ac.amu.zhcet.data.model.dto.StudentUpload;
import in.ac.amu.zhcet.data.repository.DepartmentRepository;
import in.ac.amu.zhcet.data.service.StudentService;
import in.ac.amu.zhcet.data.service.file.FileSystemStorageService;
import in.ac.amu.zhcet.data.service.file.StorageException;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;
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
public class StudentUploadService {

    private final DepartmentRepository departmentRepository;
    private final StudentService studentService;
    private final FileSystemStorageService systemStorageService;

    @Autowired
    public StudentUploadService(DepartmentRepository departmentRepository, StudentService studentService, FileSystemStorageService systemStorageService) {
        this.departmentRepository = departmentRepository;
        this.studentService = studentService;
        this.systemStorageService = systemStorageService;
    }

    @Data
    public static class UploadResult {
        @NonNull
        private List<String> errors = new ArrayList<>();
        @NonNull
        private List<StudentUpload> studentUploads = new ArrayList<>();
    }

    @Data
    public static class StudentConfirmation {
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

        CsvProcessor<StudentUpload> csvProcessor = new CsvProcessor<>(StudentUpload.class);
        try {
            List<ParseError> parseErrors = new ArrayList<>();
            List<StudentUpload> studentUploads = csvProcessor.readAll(new InputStreamReader(file.getInputStream()), parseErrors);

            if (studentUploads != null) {
                uploadResult.getStudentUploads().addAll(studentUploads);

                log.info(studentUploads.toString());
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

    private static String capitalizeFirst(String string) {
        return WordUtils.capitalizeFully(string.trim());
    }

    private static String capitalizeAll(String string) {
        return string.trim().toUpperCase(Locale.getDefault());
    }

    public StudentConfirmation confirmUpload(UploadResult uploadResult) {
        StudentConfirmation studentConfirmation = new StudentConfirmation();
        List<Department> departments = departmentRepository.findAll();

        for (StudentUpload studentUpload : uploadResult.getStudentUploads()) {
            Student student = new Student();
            student.setEnrolmentNumber(capitalizeAll(studentUpload.getEnrolmentNo()));
            student.setFacultyNumber(capitalizeAll(studentUpload.getFacultyNo()));
            student.getUser().setName(capitalizeFirst(studentUpload.getName()));

            String departmentName = capitalizeFirst(studentUpload.getDepartment());

            Optional<Department> optional = departments.stream()
                    .filter(department -> department.getName().equals(departmentName))
                    .findFirst();

            if (!optional.isPresent()) {
                studentConfirmation.studentMap.put(student, "No such department: " + departmentName);
                studentConfirmation.errors.add("Students with invalid department found");
            } else if (studentService.getByEnrolmentNumber(student.getEnrolmentNumber()) != null) {
                studentConfirmation.studentMap.put(student, "Duplicate enrolment number");
                studentConfirmation.errors.add("Students with duplicate enrolment found");
            } else if (studentService.getByFacultyNumber(student.getFacultyNumber()) != null) {
                studentConfirmation.studentMap.put(student, "Duplicate faculty number");
                studentConfirmation.errors.add("Students with duplicate faculty number found");
            } else {
                student.getUserDetails().setDepartment(optional.get());
                studentConfirmation.studentMap.put(student, null);
            }

        }

        return studentConfirmation;
    }

    @Transactional
    public void registerStudents(StudentConfirmation confirmation) {
        for (Student student : confirmation.getStudentMap().keySet()) {
            studentService.register(student);
        }
    }

    private static void logAndError(UploadResult uploadResult, String error) {
        log.error(error);
        uploadResult.errors.add(error);
    }
}
