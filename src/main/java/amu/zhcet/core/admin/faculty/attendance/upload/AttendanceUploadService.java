package amu.zhcet.core.admin.faculty.attendance.upload;

import amu.zhcet.data.attendance.Attendance;
import amu.zhcet.data.attendance.AttendanceService;
import amu.zhcet.data.course.incharge.CourseInCharge;
import amu.zhcet.storage.csv.Confirmation;
import amu.zhcet.storage.csv.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
class AttendanceUploadService {

    private final AttendanceUploadAdapter attendanceUploadAdapter;
    private final AttendanceService attendanceService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public AttendanceUploadService(
            AttendanceUploadAdapter attendanceUploadAdapter,
            AttendanceService attendanceService, ApplicationEventPublisher eventPublisher) {
        this.attendanceUploadAdapter = attendanceUploadAdapter;
        this.attendanceService = attendanceService;
        this.eventPublisher = eventPublisher;
    }

    public UploadResult<AttendanceUpload> handleUpload(MultipartFile file) throws IOException {
        return attendanceUploadAdapter.fileToUpload(file);
    }

    public Confirmation<AttendanceUpload> confirmUpload(CourseInCharge courseInCharge, UploadResult<AttendanceUpload> uploadResult) {
        return attendanceUploadAdapter.uploadToConfirmation(courseInCharge, uploadResult);
    }

    @Transactional
    public void updateAttendance(CourseInCharge courseInCharge, List<AttendanceUpload> uploadList) {
        Map<String, AttendanceUpload> attendanceUploadMap = uploadList.stream()
                .collect(Collectors.toMap(AttendanceUpload::getEnrolmentNo, item -> item));

        List<Attendance> attendances = attendanceService.getAttendanceByCourseInChargeAndStudents(courseInCharge, attendanceUploadMap.keySet());

        for (Attendance attendance : attendances) {
            AttendanceUpload attendanceUpload = attendanceUploadMap.get(attendance.getCourseRegistration().getStudent().getEnrolmentNumber());
            attendance.setDelivered(attendanceUpload.getDelivered());
            attendance.setAttended(attendanceUpload.getAttended());
        }
        attendanceService.save(attendances);

        eventPublisher.publishEvent(new AttendanceUploadEvent(courseInCharge, attendances));
    }

}
