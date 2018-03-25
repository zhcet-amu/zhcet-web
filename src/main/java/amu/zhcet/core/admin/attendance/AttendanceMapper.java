package amu.zhcet.core.admin.attendance;

import amu.zhcet.core.admin.faculty.attendance.upload.AttendanceUpload;
import amu.zhcet.data.attendance.Attendance;
import amu.zhcet.data.config.ConfigurationService;
import amu.zhcet.data.course.registration.CourseRegistration;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AttendanceMapper {

    private final ConfigurationService configurationService;

    public AttendanceMapper(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    private static float calculateSafePercentage(Attendance attendance) {
        if (attendance.getDelivered() == 0)
            return 0;

        return round(attendance.getAttended()*100/(float)attendance.getDelivered(), 2);
    }

    public AttendanceUpload fromCourseRegistration(CourseRegistration courseRegistration) {
        AttendanceUpload attendanceUpload = new AttendanceUpload();
        attendanceUpload.setEnrolmentNo(courseRegistration.getStudent().getEnrolmentNumber());
        attendanceUpload.setFacultyNo(courseRegistration.getStudent().getFacultyNumber());
        attendanceUpload.setName(courseRegistration.getStudent().getUser().getName());
        attendanceUpload.setSection(courseRegistration.getStudent().getSection());
        attendanceUpload.setAttended(courseRegistration.getAttendance().getAttended());
        attendanceUpload.setDelivered(courseRegistration.getAttendance().getDelivered());
        float percentage = calculateSafePercentage(courseRegistration.getAttendance());
        attendanceUpload.setPercentage(percentage);
        if (percentage < configurationService.getThreshold() && courseRegistration.getAttendance().getDelivered() != 0)
            attendanceUpload.setRemark("Short");

        return attendanceUpload;
    }

}
