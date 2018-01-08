package amu.zhcet.core.dean.registration.student;

import amu.zhcet.common.realtime.RealTimeStatus;
import amu.zhcet.common.realtime.RealTimeStatusService;
import amu.zhcet.data.user.student.Student;
import amu.zhcet.data.user.student.StudentService;
import amu.zhcet.storage.csv.Confirmation;
import amu.zhcet.storage.csv.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
class StudentUploadService {

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
