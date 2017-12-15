package in.ac.amu.zhcet.service.upload.csv.student;

import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.upload.StudentUpload;
import in.ac.amu.zhcet.service.StudentService;
import in.ac.amu.zhcet.service.realtime.RealTimeStatus;
import in.ac.amu.zhcet.service.realtime.RealTimeStatusService;
import in.ac.amu.zhcet.service.upload.csv.Confirmation;
import in.ac.amu.zhcet.service.upload.csv.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class StudentUploadService {

    private final StudentService studentService;
    private final StudentRegistrationAdapter studentRegistrationAdapter;
    private final RealTimeStatusService realTimeStatusService;

    @Autowired
    public StudentUploadService(StudentService studentService, StudentRegistrationAdapter studentRegistrationMapper, RealTimeStatusService realTimeStatusService) {
        this.studentService = studentService;
        this.studentRegistrationAdapter = studentRegistrationMapper;
        this.realTimeStatusService = realTimeStatusService;
    }

    public UploadResult<StudentUpload> handleUpload(MultipartFile file) throws IOException {
        return studentRegistrationAdapter.fileToUpload(file);
    }

    public Confirmation<Student> confirmUpload(UploadResult<StudentUpload> uploadResult) {
        return studentRegistrationAdapter.confirmUpload(uploadResult);
    }

    public RealTimeStatus registerStudents(Confirmation<Student> confirmation) {
        RealTimeStatus status = realTimeStatusService.install();
        studentService.register(confirmation.getData(), status);

        return status;
    }
}
