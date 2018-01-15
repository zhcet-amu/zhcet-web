package amu.zhcet.data.attendance;

import amu.zhcet.data.config.ConfigurationService;
import amu.zhcet.data.user.student.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    @Autowired
    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @Transactional
    public List<Attendance> getAttendanceByStudent(Student student) {
        return attendanceRepository.getByCourseRegistration_StudentAndCourseRegistration_FloatedCourse_Session(student, ConfigurationService.getDefaultSessionCode());
    }

}
