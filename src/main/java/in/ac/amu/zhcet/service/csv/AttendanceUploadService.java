package in.ac.amu.zhcet.service.csv;

import in.ac.amu.zhcet.data.model.CourseInCharge;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.dto.upload.AttendanceUpload;
import in.ac.amu.zhcet.service.CourseInChargeService;
import in.ac.amu.zhcet.service.CourseRegistrationService;
import in.ac.amu.zhcet.service.csv.base.AbstractUploadService;
import in.ac.amu.zhcet.service.csv.base.Confirmation;
import in.ac.amu.zhcet.service.csv.base.UploadResult;
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

    private boolean existsError;

    private final AbstractUploadService<AttendanceUpload, AttendanceUpload, Boolean> uploadService;
    private final CourseInChargeService courseInChargeService;
    private final CourseRegistrationService courseRegistrationService;

    @Autowired
    public AttendanceUploadService(AbstractUploadService<AttendanceUpload, AttendanceUpload, Boolean> uploadService, CourseInChargeService courseInChargeService, CourseRegistrationService courseRegistrationService) {
        this.uploadService = uploadService;
        this.courseInChargeService = courseInChargeService;
        this.courseRegistrationService = courseRegistrationService;
    }

    public UploadResult<AttendanceUpload> handleUpload(MultipartFile file) throws IOException {
        return uploadService.handleUpload(AttendanceUpload.class, file);
    }

    private boolean studentExists(AttendanceUpload upload, List<CourseRegistration> registrations) {
        boolean exists = registrations.stream()
                .map(registration -> registration.getStudent().getEnrolmentNumber())
                .anyMatch(enrolment -> enrolment.equals(upload.getEnrolment_no()));

        if (!exists) {
            log.warn("Student does not exist for course in-charge {}", upload.getEnrolment_no());
            existsError = true;
        }

        return exists;
    }

    public Confirmation<AttendanceUpload, Boolean> confirmUpload(String course, String section, UploadResult<AttendanceUpload> uploadResult) {
        CourseInCharge courseInCharge  = courseInChargeService.getCourseInCharge(course, section);
        List<CourseRegistration> courseRegistrations = courseInChargeService.getCourseRegistrations(courseInCharge);

        existsError = false;

        Confirmation<AttendanceUpload, Boolean> attendanceConfirmation = uploadService.confirmUpload(
                uploadResult,
                item -> item,
                upload -> studentExists(upload, courseRegistrations)
        );

        if (existsError)
            attendanceConfirmation.getErrors().add("The students highlighted in red are not registered for this course");

        return attendanceConfirmation;
    }

    @Transactional
    public void updateAttendance(String course, String section, List<AttendanceUpload> uploadList) {
        CourseInCharge courseInCharge  = courseInChargeService.getCourseInCharge(course, section);
        List<CourseRegistration> courseRegistrations = courseInChargeService.getCourseRegistrations(courseInCharge);

        for (AttendanceUpload attendanceUpload : uploadList) {
            if (!studentExists(attendanceUpload, courseRegistrations)) {
                log.error("Force updating attendance of invalid student {} {} {}", course, section, attendanceUpload.getEnrolment_no());
                throw new RuntimeException("Invalid Data : " + attendanceUpload);
            }
        }

        for (AttendanceUpload attendance : uploadList) {
            courseRegistrationService.setAttendance(course, attendance);
        }
    }

}
