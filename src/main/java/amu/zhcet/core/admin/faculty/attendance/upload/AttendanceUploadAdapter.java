package amu.zhcet.core.admin.faculty.attendance.upload;

import amu.zhcet.data.course.incharge.CourseInCharge;
import amu.zhcet.data.course.incharge.CourseInChargeService;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.storage.csv.CsvParserService;
import amu.zhcet.storage.csv.Confirmation;
import amu.zhcet.storage.csv.UploadResult;
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
    private final CsvParserService<AttendanceUpload, AttendanceUpload> uploadService;

    @Autowired
    public AttendanceUploadAdapter(CourseInChargeService courseInChargeService, CsvParserService<AttendanceUpload, AttendanceUpload> uploadService) {
        this.courseInChargeService = courseInChargeService;
        this.uploadService = uploadService;
    }

    UploadResult<AttendanceUpload> fileToUpload(MultipartFile file) throws IOException {
        return uploadService.handleUpload(AttendanceUpload.class, file);
    }

    Confirmation<AttendanceUpload> uploadToConfirmation(CourseInCharge courseInCharge, UploadResult<AttendanceUpload> uploadResult) {
        List<CourseRegistration> courseRegistrations = courseInChargeService.getCourseRegistrations(courseInCharge);

        AttendanceUploadIntegrityVerifier verifier = new AttendanceUploadIntegrityVerifier(courseRegistrations);

        Confirmation<AttendanceUpload> attendanceConfirmation =
                uploadService.confirmUpload(uploadResult)
                    .convert(item -> item)
                    .map(verifier::getError)
                    .get();

        AttendanceUploadIntegrityVerifier.ErrorConditions conditions = verifier.getErrorConditions();

        if (conditions.isDuplicateStudent())
            attendanceConfirmation.getErrors().add("Duplicate Students found");
        if (conditions.isNotRegistered())
            attendanceConfirmation.getErrors().add("Students which are not registered for this course found");

        if (!attendanceConfirmation.getErrors().isEmpty()) {
            log.warn(attendanceConfirmation.getData().toString());
        }

        return attendanceConfirmation;
    }

}
