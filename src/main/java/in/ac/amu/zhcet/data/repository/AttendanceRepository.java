package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> getByCourseRegistration_Student_EnrolmentNumberAndCourseRegistration_FloatedCourse_Session(String userId, String session);

}
