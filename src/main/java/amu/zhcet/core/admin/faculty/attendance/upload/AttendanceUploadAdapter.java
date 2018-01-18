package amu.zhcet.core.admin.faculty.attendance.upload;

import amu.zhcet.data.attendance.AttendanceUpload;
import amu.zhcet.data.course.incharge.CourseInCharge;
import amu.zhcet.data.course.incharge.CourseInChargeService;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.storage.csv.AbstractUploadService;
import amu.zhcet.storage.csv.Confirmation;
import amu.zhcet.storage.csv.UploadResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
class AttendanceUploadAdapter {

    private final CourseInChargeService courseInChargeService;
    private final AbstractUploadService<AttendanceUpload, AttendanceUpload> uploadService;

    @Data
    private static class ErrorConditions {
        private boolean exists = true;
    }

    @Autowired
    public AttendanceUploadAdapter(CourseInChargeService courseInChargeService, AbstractUploadService<AttendanceUpload, AttendanceUpload> uploadService) {
        this.courseInChargeService = courseInChargeService;
        this.uploadService = uploadService;
    }


    UploadResult<AttendanceUpload> fileToUpload(MultipartFile file) throws IOException {
        return uploadService.handleUpload(AttendanceUpload.class, file);
    }

    Confirmation<AttendanceUpload> uploadToConfirmation(CourseInCharge courseInCharge, UploadResult<AttendanceUpload> uploadResult) {
        ErrorConditions conditions = new ErrorConditions();

        List<CourseRegistration> courseRegistrations = courseInChargeService.getCourseRegistrations(courseInCharge);

        Confirmation<AttendanceUpload> attendanceConfirmation =
                uploadService.confirmUpload(uploadResult)
                    .convert(item -> item)
                    .map(upload -> studentExists(upload, courseRegistrations, conditions))
                    .get();

        if (!conditions.isExists()) {
            log.warn(attendanceConfirmation.getData().toString());
            attendanceConfirmation.getErrors().add("The students highlighted in red are not registered for this course");
        }

        return attendanceConfirmation;
    }

    static String studentExists(AttendanceUpload upload, List<CourseRegistration> registrations, ErrorConditions conditions) {
        boolean exists = registrations.stream()
                .map(registration -> registration.getStudent().getEnrolmentNumber())
                .anyMatch(enrolment -> enrolment.equals(upload.getEnrolment_no()));

        if (!exists) {
            log.info("Student does not exist for course in-charge {}", upload.getEnrolment_no());
            if (conditions != null)
                conditions.setExists(false);
        }

        return exists ? "exists" : null;
    }

}
