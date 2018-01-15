package amu.zhcet.data.attendance;

import amu.zhcet.data.user.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, String> {

    List<Attendance> getByCourseRegistration_StudentAndCourseRegistration_FloatedCourse_Session(Student student, String session);

}
