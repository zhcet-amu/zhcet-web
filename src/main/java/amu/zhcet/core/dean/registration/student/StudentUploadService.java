package amu.zhcet.core.dean.registration.student;

import amu.zhcet.common.realtime.RealTimeStatus;
import amu.zhcet.data.user.student.Student;
import amu.zhcet.data.user.student.StudentService;
import amu.zhcet.storage.csv.Confirmation;
import amu.zhcet.storage.csv.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Slf4j
@Service
class StudentUploadService {

    private final StudentService studentService;
    private final StudentRegistrationAdapter studentRegistrationAdapter;

    @Autowired
    public StudentUploadService(StudentService studentService, StudentRegistrationAdapter studentRegistrationMapper) {
        this.studentService = studentService;
        this.studentRegistrationAdapter = studentRegistrationMapper;
    }

    public UploadResult<StudentUpload> handleUpload(MultipartFile file) throws IOException {
        return studentRegistrationAdapter.fileToUpload(file);
    }

    public Confirmation<Student> confirmUpload(UploadResult<StudentUpload> uploadResult) {
        return studentRegistrationAdapter.confirmUpload(uploadResult);
    }

    @Async
    public void registerStudents(Confirmation<Student> confirmation, RealTimeStatus status) {
        Set<Student> students = confirmation.getData();

        long startTime = System.nanoTime();
        status.setContext("Student Registration");
        status.setTotal(students.size());

        try {
            students.stream()
                    .peek(ignore -> status.increment())
                    .forEach(studentService::register);
            float duration = (System.nanoTime() - startTime)/1000000f;
            status.setDuration(duration);
            status.setFinished(true);
            log.info("Saved {} Students in {} ms", students.size(), duration);
        } catch (Exception exception) {
            log.error("Error while saving students", exception);
            status.setMessage(exception.getMessage());
            status.setFailed(true);
            throw exception;
        }
    }
}
