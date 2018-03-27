package amu.zhcet.data.attendance;

import amu.zhcet.data.config.ConfigurationService;
import amu.zhcet.data.course.incharge.CourseInCharge;
import amu.zhcet.data.user.student.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    @Autowired
    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @Transactional
    public List<Attendance> getAttendanceByStudent(Student student, @Nullable String sessionCode) {
        if (sessionCode == null)
            sessionCode = ConfigurationService.getDefaultSessionCode();
        return attendanceRepository.getByCourseRegistration_StudentAndCourseRegistration_FloatedCourse_Session(student, sessionCode);
    }

    @Transactional
    public List<Attendance> getAttendanceByStudent(Student student) {
        return getAttendanceByStudent(student, null);
    }

    @Transactional
    public List<Attendance> getAttendanceByCourseInChargeAndStudents(CourseInCharge courseInCharge, Collection<String> students) {
        return attendanceRepository.getByCourseRegistration_FloatedCourseAndCourseRegistration_Student_EnrolmentNumberIn(courseInCharge.getFloatedCourse(), students);
    }

    public void save(List<Attendance> attendances) {
        attendanceRepository.saveAll(attendances);
    }
}
