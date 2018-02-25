package amu.zhcet.core.admin.faculty.attendance.upload;

import amu.zhcet.data.attendance.Attendance;
import amu.zhcet.data.course.incharge.CourseInCharge;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AttendanceUploadEvent {
    private final CourseInCharge courseInCharge;
    private final List<Attendance> attendances;
}
