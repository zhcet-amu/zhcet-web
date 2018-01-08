package amu.zhcet.data.attendance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, String> {

    List<Attendance> getByCourseRegistration_Student_EnrolmentNumberAndCourseRegistration_FloatedCourse_Session(String userId, String session);

}
