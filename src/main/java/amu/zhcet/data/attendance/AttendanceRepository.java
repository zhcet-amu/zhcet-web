package amu.zhcet.data.attendance;

import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.user.student.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface AttendanceRepository extends CrudRepository<Attendance, String> {

    List<Attendance> getByCourseRegistration_StudentAndCourseRegistration_FloatedCourse_Session(Student student, String session);

    List<Attendance> getByCourseRegistration_FloatedCourseAndCourseRegistration_Student_EnrolmentNumberIn(FloatedCourse floatedCourse, Collection<String> enrolment);

}
