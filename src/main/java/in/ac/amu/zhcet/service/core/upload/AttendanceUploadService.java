package in.ac.amu.zhcet.service.core.upload;

import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.dto.AttendanceUpload;
import in.ac.amu.zhcet.service.core.RegisteredCourseService;
import in.ac.amu.zhcet.service.core.upload.base.AbstractUploadService;
import in.ac.amu.zhcet.service.core.upload.base.Confirmation;
import in.ac.amu.zhcet.service.core.upload.base.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class AttendanceUploadService {

    private boolean uniqueError;

    private final AbstractUploadService<AttendanceUpload, AttendanceUpload, Boolean> uploadService;
    private final RegisteredCourseService registeredCourseService;

    @Autowired
    public AttendanceUploadService(AbstractUploadService<AttendanceUpload, AttendanceUpload, Boolean> uploadService, RegisteredCourseService registeredCourseService) {
        this.uploadService = uploadService;
        this.registeredCourseService = registeredCourseService;
    }

    public UploadResult<AttendanceUpload> handleUpload(MultipartFile file) throws IOException {
        return uploadService.handleUpload(AttendanceUpload.class, file);
    }

    private boolean getMappedValue(AttendanceUpload upload, String course) {
        boolean unique = registeredCourseService.exists(upload.getStudent(), course);

        if (!unique)
            uniqueError = true;

        return unique;
    }

    public Confirmation<AttendanceUpload, Boolean> confirmUpload(String course, UploadResult<AttendanceUpload> uploadResult) {
        uniqueError = false;

        Confirmation<AttendanceUpload, Boolean> attendanceConfirmation = uploadService.confirmUpload(
                uploadResult,
                item -> item,
                upload -> getMappedValue(upload, course)
        );

        if (uniqueError)
            attendanceConfirmation.getErrors().add("The students highlighted in red are not registered for this course");

        return attendanceConfirmation;
    }

    @Transactional
    public void updateAttendance(String course, List<AttendanceUpload> uploadList) {
        for (AttendanceUpload attendance : uploadList) {
            CourseRegistration courseRegistration = registeredCourseService.getByStudentAndCourse(attendance.getStudent(), course);
            registeredCourseService.setAttendance(courseRegistration, attendance.getDelivered(), attendance.getAttended());
        }
    }

}
